package wang.liangchen.matrix.framework.ddd.specification;

import wang.liangchen.matrix.framework.ddd.specification.SpecificationToken.TokenType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;

/**
 * Parses T-SQL WHERE clause strings into {@link Specification} objects.
 * <p>
 * Uses recursive-descent parsing with correct SQL operator precedence:
 * {@code NOT} &gt; {@code AND} &gt; {@code OR}.
 * </p>
 *
 * <h3>Supported SQL Syntax</h3>
 * <ul>
 *   <li>Comparison: {@code =, <>, !=, >, >=, <, <=}</li>
 *   <li>Range: {@code BETWEEN ... AND ...}, {@code NOT BETWEEN ... AND ...}</li>
 *   <li>Set: {@code IN (...)}, {@code NOT IN (...)}</li>
 *   <li>Pattern: {@code LIKE '...'}, {@code NOT LIKE '...'}</li>
 *   <li>Null: {@code IS NULL}, {@code IS NOT NULL}</li>
 *   <li>Case-insensitive: {@code UPPER(field) = UPPER('value')}, {@code UPPER(field) <> UPPER('value')},
 *       {@code UPPER(field) LIKE UPPER('pattern')}</li>
 *   <li>Logical: {@code AND}, {@code OR}, {@code NOT}, parentheses {@code ()}</li>
 * </ul>
 *
 * <h3>Value Types</h3>
 * <ul>
 *   <li>Strings: single-quoted, e.g. {@code 'Alice'}</li>
 *   <li>Numbers: integer or decimal, e.g. {@code 30}, {@code 95000.50}</li>
 *   <li>Typed dates/times: single-quoted ISO-8601 values, e.g. {@code '2026-04-11'} or {@code '2026-04-11T08:30:00'}</li>
 *   <li>Null: {@code NULL} keyword</li>
 * </ul>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
     * Map<String, Function<User, ?>> fields = new HashMap<>();
     * fields.put("name", User::getName);
     * fields.put("age", User::getAge);
     * fields.put("role", User::getRole);
 *
 * Specification<User> spec = SpecificationParser.parse(
 *     "name = 'Alice' AND age > 30 AND (role IN ('ADMIN', 'USER') OR name LIKE 'B%')",
 *     fields
 * );
 *
 * boolean matched = spec.isSatisfiedBy(user);
 * }</pre>
 *
 * @author Liangchen.Wang
 */
public final class SpecificationParser<T> {

    private final List<SpecificationToken> tokens;
    private final Map<String, ResolvedField<T>> fields;
    private int cursor;

    private SpecificationParser(List<SpecificationToken> tokens, Map<String, ResolvedField<T>> fields) {
        this.tokens = tokens;
        this.fields = fields;
        this.cursor = 0;
    }

    /**
     * Parses a T-SQL WHERE clause string into a {@link Specification}.
     *
     * @param sql            the SQL condition string (without the WHERE keyword)
     * @param fieldResolvers a map of field names (case-insensitive) to extractor functions
     * @param <T>            the candidate object type
     * @return the parsed specification
     * @throws IllegalArgumentException if the SQL syntax is invalid or a field name is unknown
     */
    public static <T> Specification<T> parse(String sql, Map<String, Function<T, ?>> fieldResolvers) {
        Objects.requireNonNull(fieldResolvers, "fieldResolvers must not be null");
        return doParse(sql, buildLegacyFields(fieldResolvers));
    }

    /**
     * Parses a T-SQL WHERE clause string into a {@link Specification} using typed field metadata.
     * <p>
     * When field types are provided, literals are converted to the target Java type before comparison.
     * This enables safer parsing for booleans and numeric fields and supports SQL-like round-tripping of
     * diagnostics such as {@code active = 1}.</p>
     *
     * @param sql    the SQL condition string (without the WHERE keyword)
     * @param fields typed field metadata
     * @param <T>    the candidate object type
     * @return the parsed specification
     */
    public static <T> Specification<T> parse(String sql, Collection<SpecificationField<T, ?>> fields) {
        Objects.requireNonNull(fields, "fields must not be null");
        return doParse(sql, buildTypedFields(fields));
    }

    /**
     * Convenience overload for parsing with typed field metadata.
     *
     * @param sql    the SQL condition string (without the WHERE keyword)
     * @param fields typed field metadata
     * @param <T>    the candidate object type
     * @return the parsed specification
     */
    @SafeVarargs
    public static <T> Specification<T> parse(String sql, SpecificationField<T, ?>... fields) {
        Objects.requireNonNull(fields, "fields must not be null");
        return parse(sql, Arrays.asList(fields));
    }

    private static <T> Specification<T> doParse(String sql, Map<String, ResolvedField<T>> fields) {
        Objects.requireNonNull(sql, "sql must not be null");
        if (sql.trim().isEmpty()) {
            return Specification.any();
        }
        List<SpecificationToken> tokens = SpecificationTokenizer.tokenize(sql);
        SpecificationParser<T> parser = new SpecificationParser<>(tokens, fields);
        Specification<T> result = parser.parseExpression();
        parser.expect(TokenType.EOF, "Unexpected token after expression");
        return result;
    }

    // ==================== Recursive-Descent Grammar ====================

    // expression → andExpression (OR andExpression)*
    private Specification<T> parseExpression() {
        Specification<T> left = parseAndExpression();
        while (check(TokenType.OR)) {
            advance();
            Specification<T> right = parseAndExpression();
            left = left.or(right);
        }
        return left;
    }

    // andExpression → notExpression (AND notExpression)*
    private Specification<T> parseAndExpression() {
        Specification<T> left = parseNotExpression();
        while (check(TokenType.AND)) {
            advance();
            Specification<T> right = parseNotExpression();
            left = left.and(right);
        }
        return left;
    }

    // notExpression → NOT* primary
    private Specification<T> parseNotExpression() {
        if (check(TokenType.NOT)) {
            // Peek ahead: NOT followed by BETWEEN/IN/LIKE is handled in parsePrimary
            SpecificationToken next = peekAt(1);
            if (next != null && (next.type() == TokenType.BETWEEN
                    || next.type() == TokenType.IN
                    || next.type() == TokenType.LIKE)) {
                // Let parsePrimary handle NOT BETWEEN / NOT IN / NOT LIKE
                return parsePrimary();
            }
            advance(); // consume NOT
            Specification<T> inner = parseNotExpression();
            return inner.not();
        }
        return parsePrimary();
    }

    // primary → '(' expression ')' | upperPattern | fieldComparison
    private Specification<T> parsePrimary() {
        // Parenthesized group
        if (check(TokenType.LPAREN)) {
            advance();
            Specification<T> expr = parseExpression();
            expect(TokenType.RPAREN, "Expected ')' to close parenthesized expression");
            return expr;
        }

        // Constant predicate, for round-tripping tautologies such as 1 = 1 and 1 = 0.
        if (isLiteral(peek().type())) {
            return parseConstantComparison();
        }

        // UPPER(field) op UPPER('value')
        if (check(TokenType.UPPER)) {
            return parseUpperPattern();
        }

        // Field comparison: identifier followed by operator
        if (check(TokenType.IDENTIFIER)) {
            return parseFieldComparison();
        }

        throw parseError("Expected field name, '(', 'NOT', or 'UPPER' but found: " + peek());
    }

    // ==================== UPPER() Pattern ====================

    // UPPER(field) = UPPER('value')
    // UPPER(field) <> UPPER('value')
    // UPPER(field) LIKE UPPER('pattern')
    private Specification<T> parseUpperPattern() {
        advance(); // consume UPPER
        expect(TokenType.LPAREN, "Expected '(' after UPPER");
        String fieldName = expectIdentifier("Expected field name inside UPPER()");
        expect(TokenType.RPAREN, "Expected ')' after field name in UPPER()");

        ResolvedField<T> field = resolveField(fieldName);
        ensureStringLike(field, "UPPER");
        FieldSpecification<T, Object> fieldSpec = field.toFieldSpecification();

        SpecificationToken opToken = peek();
        if (check(TokenType.EQ)) {
            advance();
            String value = parseUpperValue();
            return fieldSpec.equalToIgnoreCase(value);
        } else if (check(TokenType.NEQ)) {
            advance();
            String value = parseUpperValue();
            return fieldSpec.notEqualToIgnoreCase(value);
        } else if (check(TokenType.LIKE)) {
            advance();
            String pattern = parseUpperValue();
            return fieldSpec.likeIgnoreCase(pattern);
        }

        throw parseError("Expected '=', '<>' or 'LIKE' after UPPER(field), but found: " + opToken);
    }

    private String parseUpperValue() {
        expect(TokenType.UPPER, "Expected UPPER on the right side");
        expect(TokenType.LPAREN, "Expected '(' after UPPER");
        SpecificationToken valueToken = peek();
        if (valueToken.type() != TokenType.STRING_LITERAL) {
            throw parseError("Expected string literal inside UPPER(), but found: " + valueToken);
        }
        advance();
        expect(TokenType.RPAREN, "Expected ')' after value in UPPER()");
        return valueToken.value();
    }

    // ==================== Field Comparison ====================

    private Specification<T> parseFieldComparison() {
        String fieldName = peek().value();
        advance(); // consume identifier

        ResolvedField<T> field = resolveField(fieldName);
        FieldSpecification<T, Object> fieldSpec = field.toFieldSpecification();

        SpecificationToken opToken = peek();

        // IS NULL / IS NOT NULL
        if (check(TokenType.IS)) {
            advance();
            if (check(TokenType.NOT)) {
                advance();
                expectKeyword(TokenType.NULL, "Expected NULL after IS NOT");
                return fieldSpec.isNotNull();
            }
            expectKeyword(TokenType.NULL, "Expected NULL or NOT NULL after IS");
            return fieldSpec.isNull();
        }

        // NOT BETWEEN / NOT IN / NOT LIKE
        if (check(TokenType.NOT)) {
            advance();
            SpecificationToken afterNot = peek();
            if (check(TokenType.BETWEEN)) {
                advance();
                Object low = parseValueFor(field);
                expectKeyword(TokenType.AND, "Expected AND in BETWEEN ... AND ...");
                Object high = parseValueFor(field);
                return fieldSpec.notBetween(low, high);
            } else if (check(TokenType.IN)) {
                advance();
                List<Object> values = parseInList(field);
                return fieldSpec.notIn(values);
            } else if (check(TokenType.LIKE)) {
                ensureStringLike(field, "LIKE");
                advance();
                String pattern = parseStringValue();
                return fieldSpec.notLike(pattern);
            }
            throw parseError("Expected BETWEEN, IN, or LIKE after NOT, but found: " + afterNot);
        }

        // BETWEEN ... AND ...
        if (check(TokenType.BETWEEN)) {
            advance();
            Object low = parseValueFor(field);
            expectKeyword(TokenType.AND, "Expected AND in BETWEEN ... AND ...");
            Object high = parseValueFor(field);
            return fieldSpec.between(low, high);
        }

        // IN (...)
        if (check(TokenType.IN)) {
            advance();
            List<Object> values = parseInList(field);
            return fieldSpec.in(values);
        }

        // LIKE 'pattern'
        if (check(TokenType.LIKE)) {
            ensureStringLike(field, "LIKE");
            advance();
            String pattern = parseStringValue();
            return fieldSpec.like(pattern);
        }

        // Comparison operators: =, <>, !=, >, >=, <, <=
        switch (opToken.type()) {
            case EQ: {
                advance();
                Object value = parseValueFor(field);
                return fieldSpec.equalTo(value);
            }
            case NEQ: {
                advance();
                Object value = parseValueFor(field);
                return fieldSpec.notEqualTo(value);
            }
            case GT: {
                advance();
                Object value = parseValueFor(field);
                return fieldSpec.greaterThan(value);
            }
            case GTE: {
                advance();
                Object value = parseValueFor(field);
                return fieldSpec.greaterThanOrEqual(value);
            }
            case LT: {
                advance();
                Object value = parseValueFor(field);
                return fieldSpec.lessThan(value);
            }
            case LTE: {
                advance();
                Object value = parseValueFor(field);
                return fieldSpec.lessThanOrEqual(value);
            }
            default:
                throw parseError("Expected operator after field '" + fieldName + "', but found: " + opToken);
        }
    }

    // ==================== Value Parsing ====================

    private Specification<T> parseConstantComparison() {
        Object left = convertUntypedValue(peek());
        advance();

        SpecificationToken op = peek();
        if (op.type() != TokenType.EQ && op.type() != TokenType.NEQ) {
            throw parseErrorAt(op, "Expected '=' or '<>' after constant value, but found: " + op);
        }
        advance();

        SpecificationToken rightToken = peek();
        if (!isLiteral(rightToken.type())) {
            throw parseErrorAt(rightToken, "Expected constant value, but found: " + rightToken);
        }
        Object right = convertUntypedValue(rightToken);
        advance();

        boolean satisfied = op.type() == TokenType.EQ ? Objects.equals(left, right) : !Objects.equals(left, right);
        return satisfied ? Specification.<T>any() : Specification.<T>none();
    }

    private boolean isLiteral(TokenType type) {
        return type == TokenType.STRING_LITERAL
                || type == TokenType.NUMBER_LITERAL
                || type == TokenType.NULL
                || type == TokenType.TRUE
                || type == TokenType.FALSE;
    }

    private Object parseValueFor(ResolvedField<T> field) {
        SpecificationToken token = peek();
        Object value = field.typed() ? convertTypedValue(field, token) : convertUntypedValue(token);
        advance();
        return value;
    }

    private Object convertUntypedValue(SpecificationToken token) {
        switch (token.type()) {
            case STRING_LITERAL:
                return token.value();
            case NUMBER_LITERAL:
                return parseNumber(token.value());
            case NULL:
                return null;
            case TRUE:
                return true;
            case FALSE:
                return false;
            default:
                throw parseErrorAt(token, "Expected value (string, number, boolean, or NULL), but found: " + token);
        }
    }

    private Object convertTypedValue(ResolvedField<T> field, SpecificationToken token) {
        switch (token.type()) {
            case NULL:
                return null;
            case IDENTIFIER:
                return convertIdentifierLiteral(field, token);
            case STRING_LITERAL:
                return convertStringLiteral(field, token);
            case NUMBER_LITERAL:
                return convertNumberLiteral(field, token);
            case TRUE:
                return convertBooleanLiteral(field, true, token);
            case FALSE:
                return convertBooleanLiteral(field, false, token);
            default:
                throw parseErrorAt(token, "Expected value for field '" + field.name()
                        + "' of type " + field.type().getSimpleName() + ", but found: " + token);
        }
    }

    private String parseStringValue() {
        SpecificationToken token = peek();
        if (token.type() != TokenType.STRING_LITERAL) {
            throw parseErrorAt(token, "Expected string literal, but found: " + token);
        }
        advance();
        return token.value();
    }

    private List<Object> parseInList(ResolvedField<T> field) {
        expect(TokenType.LPAREN, "Expected '(' after IN");
        List<Object> values = new ArrayList<>();
        values.add(parseValueFor(field));
        while (check(TokenType.COMMA)) {
            advance();
            values.add(parseValueFor(field));
        }
        expect(TokenType.RPAREN, "Expected ')' to close IN list");
        return values;
    }

    private static Object parseNumber(String text) {
        if (text.contains(".")) {
            return new BigDecimal(text);
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException e2) {
                return new BigDecimal(text);
            }
        }
    }

    // ==================== Field Resolution ====================

    private ResolvedField<T> resolveField(String fieldName) {
        ResolvedField<T> field = fields.get(fieldName);
        if (field == null) {
            throw parseError("Unknown field: '" + fieldName
                    + "'. Available fields: " + fields.keySet());
        }
        return field;
    }

    // ==================== Token Navigation ====================

    private SpecificationToken peek() {
        return tokens.get(cursor);
    }

    private SpecificationToken peekAt(int offset) {
        int idx = cursor + offset;
        return idx < tokens.size() ? tokens.get(idx) : null;
    }

    private boolean check(TokenType type) {
        return peek().type() == type;
    }

    private SpecificationToken advance() {
        SpecificationToken token = tokens.get(cursor);
        if (token.type() != TokenType.EOF) {
            cursor++;
        }
        return token;
    }

    private void expect(TokenType type, String errorMessage) {
        if (!check(type)) {
            throw parseError(errorMessage + ". Found: " + peek());
        }
        advance();
    }

    private void expectKeyword(TokenType type, String errorMessage) {
        expect(type, errorMessage);
    }

    private String expectIdentifier(String errorMessage) {
        if (!check(TokenType.IDENTIFIER)) {
            throw parseError(errorMessage + ". Found: " + peek());
        }
        return advance().value();
    }

    private Object convertStringLiteral(ResolvedField<T> field, SpecificationToken token) {
        Class<?> type = field.type();
        if (isStringLike(type)) {
            return token.value();
        }
        if (LocalDate.class.equals(type)) {
            return parseLocalDateLiteral(token.value(), field, token);
        }
        if (LocalDateTime.class.equals(type)) {
            return parseLocalDateTimeLiteral(token.value(), field, token);
        }
        if (type.isEnum()) {
            return parseEnumLiteral(type, token.value(), token);
        }
        throw parseErrorAt(token, "Field '" + field.name() + "' expects " + type.getSimpleName()
                + ", but found string literal");
    }

    private Object convertIdentifierLiteral(ResolvedField<T> field, SpecificationToken token) {
        if (field.type().isEnum()) {
            return parseEnumLiteral(field.type(), token.value(), token);
        }
        throw parseErrorAt(token, "Field '" + field.name() + "' expects " + field.type().getSimpleName()
                + ", but found identifier literal: " + token.value());
    }

    private Object convertNumberLiteral(ResolvedField<T> field, SpecificationToken token) {
        Class<?> type = field.type();
        String text = token.value();
        try {
            if (Boolean.class.equals(type)) {
                if ("1".equals(text)) {
                    return true;
                }
                if ("0".equals(text)) {
                    return false;
                }
                throw parseErrorAt(token,
                        "Boolean field '" + field.name() + "' accepts only TRUE/FALSE or numeric 1/0, but found: " + text);
            }
            if (BigDecimal.class.equals(type)) {
                return new BigDecimal(text);
            }
            if (Integer.class.equals(type)) {
                return Integer.parseInt(requireIntegral(text, token, field));
            }
            if (Long.class.equals(type)) {
                return Long.parseLong(requireIntegral(text, token, field));
            }
            if (Short.class.equals(type)) {
                return Short.parseShort(requireIntegral(text, token, field));
            }
            if (Byte.class.equals(type)) {
                return Byte.parseByte(requireIntegral(text, token, field));
            }
            if (Double.class.equals(type)) {
                return Double.parseDouble(text);
            }
            if (Float.class.equals(type)) {
                return Float.parseFloat(text);
            }
            if (Number.class.equals(type)) {
                return parseNumber(text);
            }
        } catch (NumberFormatException e) {
            throw parseErrorAt(token, "Value '" + text + "' cannot be converted to " + type.getSimpleName()
                    + " for field '" + field.name() + "'");
        }
        throw parseErrorAt(token, "Field '" + field.name() + "' expects " + type.getSimpleName()
                + ", but found numeric literal");
    }

    private Object convertBooleanLiteral(ResolvedField<T> field, boolean value, SpecificationToken token) {
        if (Boolean.class.equals(field.type())) {
            return value;
        }
        throw parseErrorAt(token, "Field '" + field.name() + "' expects " + field.type().getSimpleName()
                + ", but found boolean literal");
    }

    private static boolean isStringLike(Class<?> type) {
        return String.class.equals(type) || CharSequence.class.isAssignableFrom(type);
    }

    private void ensureStringLike(ResolvedField<T> field, String operator) {
        if (field.typed() && !isStringLike(field.type())) {
            throw parseError(operator + " can only be used with string-like fields, but '"
                    + field.name() + "' is " + field.type().getSimpleName());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object parseEnumLiteral(Class<?> enumType, String text, SpecificationToken token) {
        try {
            return Enum.valueOf((Class<? extends Enum>) enumType.asSubclass(Enum.class), text);
        } catch (IllegalArgumentException ex) {
            throw parseErrorAt(token, "Unknown enum constant '" + text + "' for type " + enumType.getSimpleName());
        }
    }

    private String requireIntegral(String text, SpecificationToken token, ResolvedField<T> field) {
        if (text.contains(".")) {
            throw parseErrorAt(token, "Field '" + field.name() + "' expects integral " + field.type().getSimpleName()
                    + ", but found decimal literal: " + text);
        }
        return text;
    }

    private LocalDate parseLocalDateLiteral(String text, ResolvedField<T> field, SpecificationToken token) {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException ex) {
            throw parseErrorAt(token, "Value '" + text + "' cannot be converted to LocalDate for field '"
                    + field.name() + "'. Expected ISO-8601 format such as 2026-04-11");
        }
    }

    private LocalDateTime parseLocalDateTimeLiteral(String text, ResolvedField<T> field, SpecificationToken token) {
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException ex) {
            throw parseErrorAt(token, "Value '" + text + "' cannot be converted to LocalDateTime for field '"
                    + field.name() + "'. Expected ISO-8601 format such as 2026-04-11T08:30:00");
        }
    }

    private static <T> Map<String, ResolvedField<T>> buildLegacyFields(Map<String, Function<T, ?>> fieldResolvers) {
        Map<String, ResolvedField<T>> ciFields = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        fieldResolvers.forEach((name, extractor) -> {
            Objects.requireNonNull(name, "field name must not be null");
            Objects.requireNonNull(extractor, () -> "extractor for field '" + name + "' must not be null");
            ciFields.put(name, ResolvedField.legacy(name, extractor));
        });
        return ciFields;
    }

    private static <T> Map<String, ResolvedField<T>> buildTypedFields(Collection<SpecificationField<T, ?>> fields) {
        Map<String, ResolvedField<T>> ciFields = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (SpecificationField<T, ?> field : fields) {
            Objects.requireNonNull(field, "fields must not contain null elements");
            ResolvedField<T> resolved = ResolvedField.typed(field);
            ResolvedField<T> previous = ciFields.putIfAbsent(resolved.name(), resolved);
            if (previous != null) {
                throw new IllegalArgumentException("Duplicate field name: '" + resolved.name() + "'");
            }
        }
        return ciFields;
    }

    private IllegalArgumentException parseError(String message) {
        SpecificationToken token = peek();
        return parseErrorAt(token, message);
    }

    private IllegalArgumentException parseErrorAt(SpecificationToken token, String message) {
        return new IllegalArgumentException("Parse error at position " + token.position() + ": " + message);
    }

    private static final class ResolvedField<T> {
        private final String name;
        private final Class<?> type;
        private final Function<T, ?> extractor;
        private final boolean typed;

        private ResolvedField(String name, Class<?> type, Function<T, ?> extractor, boolean typed) {
            this.name = name;
            this.type = type;
            this.extractor = extractor;
            this.typed = typed;
        }

        static <T> ResolvedField<T> legacy(String name, Function<T, ?> extractor) {
            return new ResolvedField<>(name, Object.class, extractor, false);
        }

        static <T> ResolvedField<T> typed(SpecificationField<T, ?> field) {
            return new ResolvedField<>(field.name(), box(field.type()), field.extractor(), true);
        }

        @SuppressWarnings("unchecked")
        FieldSpecification<T, Object> toFieldSpecification() {
            return Specification.field((Function<T, Object>) extractor, name);
        }

        String name() {
            return name;
        }

        Class<?> type() {
            return type;
        }

        boolean typed() {
            return typed;
        }

        private static Class<?> box(Class<?> type) {
            if (!type.isPrimitive()) {
                return type;
            }
            if (type == boolean.class) return Boolean.class;
            if (type == byte.class) return Byte.class;
            if (type == short.class) return Short.class;
            if (type == int.class) return Integer.class;
            if (type == long.class) return Long.class;
            if (type == float.class) return Float.class;
            if (type == double.class) return Double.class;
            if (type == char.class) return Character.class;
            return type;
        }
    }
}

