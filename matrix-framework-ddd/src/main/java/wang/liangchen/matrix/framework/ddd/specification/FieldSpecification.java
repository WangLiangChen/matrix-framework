package wang.liangchen.matrix.framework.ddd.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Field-level specification builder — provides SQL-style comparison operators
 * on a specific field extracted from the candidate object.
 * <p>
 * Created via {@link Specification#field(Function)} or {@link Specification#field(Function, String)}.
 * </p>
 *
 * <h3>Supported Operators</h3>
 * <table>
 *   <tr><th>SQL</th><th>Method</th></tr>
 *   <tr><td>{@code =}</td><td>{@link #equalTo(Object)}, {@link #equalToIgnoreCase(String)}</td></tr>
 *   <tr><td>{@code <>}</td><td>{@link #notEqualTo(Object)}, {@link #notEqualToIgnoreCase(String)}</td></tr>
 *   <tr><td>{@code >}</td><td>{@link #greaterThan(Object)}</td></tr>
 *   <tr><td>{@code >=}</td><td>{@link #greaterThanOrEqual(Object)}</td></tr>
 *   <tr><td>{@code <}</td><td>{@link #lessThan(Object)}</td></tr>
 *   <tr><td>{@code <=}</td><td>{@link #lessThanOrEqual(Object)}</td></tr>
 *   <tr><td>{@code BETWEEN ... AND ...}</td><td>{@link #between(Object, Object)}</td></tr>
 *   <tr><td>{@code NOT BETWEEN ... AND ...}</td><td>{@link #notBetween(Object, Object)}</td></tr>
 *   <tr><td>{@code IN (...)}</td><td>{@link #in(Object[])}, {@link #in(Collection)}</td></tr>
 *   <tr><td>{@code NOT IN (...)}</td><td>{@link #notIn(Object[])}, {@link #notIn(Collection)}</td></tr>
 *   <tr><td>{@code LIKE}</td><td>{@link #like(String)}</td></tr>
 *   <tr><td>{@code NOT LIKE}</td><td>{@link #notLike(String)}</td></tr>
 *   <tr><td>{@code IS NULL}</td><td>{@link #isNull()}</td></tr>
 *   <tr><td>{@code IS NOT NULL}</td><td>{@link #isNotNull()}</td></tr>
 * </table>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * Specification<User> spec = Specification.where(
     *         Specification.<User, String>field(User::getName, "name").like("A%")
 *     ).and(
     *         Specification.<User, Integer>field(User::getAge, "age").between(18, 65)
 *     ).and(
     *         Specification.<User, String>field(User::getRole, "role").in("ADMIN", "MODERATOR")
 *     );
 *
 * boolean matched = spec.isSatisfiedBy(user);
 * }</pre>
 *
 * <h3>LIKE Pattern Syntax</h3>
 * <ul>
 *   <li>{@code %} — matches any sequence of zero or more characters</li>
 *   <li>{@code _} — matches any single character</li>
 * </ul>
 * <p>Matching is <b>case-sensitive</b> by default. Use {@link #likeIgnoreCase(String)} for case-insensitive matching.</p>
 *
 * @param <T> the type of the candidate object
 * @param <V> the type of the field value
 * @author Liangchen.Wang
 */
public final class FieldSpecification<T, V> {

    private final Function<T, V> extractor;
    private final String fieldName;

    FieldSpecification(Function<T, V> extractor, String fieldName) {
        this.extractor = Objects.requireNonNull(extractor, "extractor must not be null");
        this.fieldName = fieldName != null ? fieldName : "field";
    }

    // ==================== Equality: =, != ====================

    /**
     * T-SQL: {@code field = value} or {@code field IS NULL} when value is null.
     * <p>Null-safe: both sides {@code null} → {@code true}.</p>
     */
    public Specification<T> equalTo(V value) {
        return Specification.of(
                candidate -> Objects.equals(extractor.apply(candidate), value),
                value == null ? fieldName + " IS NULL" : fieldName + " = " + formatValue(value));
    }

    /**
     * T-SQL: {@code field <> value} or {@code field IS NOT NULL} when value is null.
     */
    public Specification<T> notEqualTo(V value) {
        return Specification.of(
                candidate -> !Objects.equals(extractor.apply(candidate), value),
                value == null ? fieldName + " IS NOT NULL" : fieldName + " <> " + formatValue(value));
    }

    /**
     * Case-insensitive variant of {@link #equalTo(Object)}.
     * <p>SQL equivalent: {@code UPPER(field) = UPPER('value')}</p>
     * <p>Field value is converted via {@code toString()} and compared using {@link String#equalsIgnoreCase(String)}.
     * Returns {@code false} if field is {@code null}.</p>
     */
    public Specification<T> equalToIgnoreCase(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return Specification.of(candidate -> {
            V fieldValue = extractor.apply(candidate);
            return fieldValue != null && fieldValue.toString().equalsIgnoreCase(value);
        }, "UPPER(" + fieldName + ") = UPPER(" + formatValue(value) + ")");
    }

    /**
     * Case-insensitive variant of {@link #notEqualTo(Object)}.
     * <p>SQL equivalent: {@code UPPER(field) <> UPPER('value')}</p>
     * <p>Field value is converted via {@code toString()} and compared using {@link String#equalsIgnoreCase(String)}.
     * Returns {@code true} if field is {@code null} (consistent with SQL: NULL &lt;&gt; 'x' is UNKNOWN, treated as not equal).</p>
     */
    public Specification<T> notEqualToIgnoreCase(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return Specification.of(candidate -> {
            V fieldValue = extractor.apply(candidate);
            return fieldValue == null || !fieldValue.toString().equalsIgnoreCase(value);
        }, "UPPER(" + fieldName + ") <> UPPER(" + formatValue(value) + ")");
    }

    // ==================== Null: IS NULL, IS NOT NULL ====================

    /**
     * SQL: {@code field IS NULL}
     */
    public Specification<T> isNull() {
        return Specification.of(
                candidate -> extractor.apply(candidate) == null,
                fieldName + " IS NULL");
    }

    /**
     * SQL: {@code field IS NOT NULL}
     */
    public Specification<T> isNotNull() {
        return Specification.of(
                candidate -> extractor.apply(candidate) != null,
                fieldName + " IS NOT NULL");
    }

    // ==================== IN, NOT IN ====================

    /**
     * SQL: {@code field IN (v1, v2, ...)}
     */
    @SafeVarargs
    public final Specification<T> in(V... values) {
        Objects.requireNonNull(values, "values must not be null");
        List<V> valueList = Arrays.asList(values);
        validateInValues(valueList);
        Set<V> set = new HashSet<>(valueList);
        return Specification.of(
                candidate -> set.contains(extractor.apply(candidate)),
                fieldName + " IN (" + formatValues(valueList) + ")");
    }

    /**
     * SQL: {@code field IN (v1, v2, ...)}
     */
    public Specification<T> in(Collection<V> values) {
        Objects.requireNonNull(values, "values must not be null");
        List<V> valueList = new ArrayList<>(values);
        validateInValues(valueList);
        Set<V> set = new HashSet<>(valueList);
        return Specification.of(
                candidate -> set.contains(extractor.apply(candidate)),
                fieldName + " IN (" + formatValues(valueList) + ")");
    }

    /**
     * SQL: {@code field NOT IN (v1, v2, ...)}
     */
    @SafeVarargs
    public final Specification<T> notIn(V... values) {
        Objects.requireNonNull(values, "values must not be null");
        List<V> valueList = Arrays.asList(values);
        validateInValues(valueList);
        Set<V> set = new HashSet<>(valueList);
        return Specification.of(
                candidate -> !set.contains(extractor.apply(candidate)),
                fieldName + " NOT IN (" + formatValues(valueList) + ")");
    }

    /**
     * SQL: {@code field NOT IN (v1, v2, ...)}
     */
    public Specification<T> notIn(Collection<V> values) {
        Objects.requireNonNull(values, "values must not be null");
        List<V> valueList = new ArrayList<>(values);
        validateInValues(valueList);
        Set<V> set = new HashSet<>(valueList);
        return Specification.of(
                candidate -> !set.contains(extractor.apply(candidate)),
                fieldName + " NOT IN (" + formatValues(valueList) + ")");
    }

    // ==================== LIKE, NOT LIKE ====================

    /**
     * SQL: {@code field LIKE 'pattern'} (case-sensitive).
     * <p>Pattern syntax: {@code %} = any chars, {@code _} = single char.</p>
     * <p>Field value is converted via {@code toString()}. Returns {@code false} if field is {@code null}.</p>
     */
    public Specification<T> like(String pattern) {
        Objects.requireNonNull(pattern, "pattern must not be null");
        Pattern regex = likeToRegex(pattern, false);
        return Specification.of(candidate -> {
            V val = extractor.apply(candidate);
            return val != null && regex.matcher(val.toString()).matches();
        }, fieldName + " LIKE " + formatValue(pattern));
    }

    /**
     * SQL: {@code field NOT LIKE 'pattern'} (case-sensitive).
     */
    public Specification<T> notLike(String pattern) {
        Objects.requireNonNull(pattern, "pattern must not be null");
        Pattern regex = likeToRegex(pattern, false);
        return Specification.of(candidate -> {
            V val = extractor.apply(candidate);
            return val != null && !regex.matcher(val.toString()).matches();
        }, fieldName + " NOT LIKE " + formatValue(pattern));
    }

    /**
     * Case-insensitive variant of {@link #like(String)}.
     * <p>SQL equivalent: {@code UPPER(field) LIKE UPPER('pattern')}</p>
     */
    public Specification<T> likeIgnoreCase(String pattern) {
        Objects.requireNonNull(pattern, "pattern must not be null");
        Pattern regex = likeToRegex(pattern, true);
        return Specification.of(candidate -> {
            V val = extractor.apply(candidate);
            return val != null && regex.matcher(val.toString()).matches();
        }, "UPPER(" + fieldName + ") LIKE UPPER(" + formatValue(pattern) + ")");
    }

    // ==================== String Convenience ====================

    /**
     * Equivalent to SQL: {@code field LIKE 'prefix%'}
     */
    public Specification<T> startsWith(String prefix) {
        Objects.requireNonNull(prefix, "prefix must not be null");
        return Specification.of(candidate -> {
            V val = extractor.apply(candidate);
            return val != null && val.toString().startsWith(prefix);
        }, fieldName + " LIKE " + formatValue(prefix + "%"));
    }

    /**
     * Equivalent to SQL: {@code field LIKE '%suffix'}
     */
    public Specification<T> endsWith(String suffix) {
        Objects.requireNonNull(suffix, "suffix must not be null");
        return Specification.of(candidate -> {
            V val = extractor.apply(candidate);
            return val != null && val.toString().endsWith(suffix);
        }, fieldName + " LIKE " + formatValue("%" + suffix));
    }

    /**
     * Equivalent to SQL: {@code field LIKE '%substring%'}
     */
    public Specification<T> contains(String substring) {
        Objects.requireNonNull(substring, "substring must not be null");
        return Specification.of(candidate -> {
            V val = extractor.apply(candidate);
            return val != null && val.toString().contains(substring);
        }, fieldName + " LIKE " + formatValue("%" + substring + "%"));
    }

    // ==================== Comparable: >, >=, <, <=, BETWEEN ====================

    /**
     * SQL: {@code field > value}
     * <p>Field value must implement {@link Comparable}. Returns {@code false} if field is {@code null}.</p>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Specification<T> greaterThan(V value) {
        Objects.requireNonNull(value, "value must not be null");
        return Specification.of(candidate -> {
            V fieldValue = extractor.apply(candidate);
            if (fieldValue == null) return false;
            return ((Comparable) fieldValue).compareTo(value) > 0;
        }, fieldName + " > " + formatValue(value));
    }

    /**
     * SQL: {@code field >= value}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Specification<T> greaterThanOrEqual(V value) {
        Objects.requireNonNull(value, "value must not be null");
        return Specification.of(candidate -> {
            V fieldValue = extractor.apply(candidate);
            if (fieldValue == null) return false;
            return ((Comparable) fieldValue).compareTo(value) >= 0;
        }, fieldName + " >= " + formatValue(value));
    }

    /**
     * SQL: {@code field < value}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Specification<T> lessThan(V value) {
        Objects.requireNonNull(value, "value must not be null");
        return Specification.of(candidate -> {
            V fieldValue = extractor.apply(candidate);
            if (fieldValue == null) return false;
            return ((Comparable) fieldValue).compareTo(value) < 0;
        }, fieldName + " < " + formatValue(value));
    }

    /**
     * SQL: {@code field <= value}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Specification<T> lessThanOrEqual(V value) {
        Objects.requireNonNull(value, "value must not be null");
        return Specification.of(candidate -> {
            V fieldValue = extractor.apply(candidate);
            if (fieldValue == null) return false;
            return ((Comparable) fieldValue).compareTo(value) <= 0;
        }, fieldName + " <= " + formatValue(value));
    }

    /**
     * SQL: {@code field BETWEEN low AND high} (inclusive on both ends).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Specification<T> between(V low, V high) {
        Objects.requireNonNull(low, "low must not be null");
        Objects.requireNonNull(high, "high must not be null");
        validateRangeBounds(low, high);
        return Specification.of(candidate -> {
            V fieldValue = extractor.apply(candidate);
            if (fieldValue == null) return false;
            Comparable comp = (Comparable) fieldValue;
            return comp.compareTo(low) >= 0 && comp.compareTo(high) <= 0;
        }, fieldName + " BETWEEN " + formatValue(low) + " AND " + formatValue(high));
    }

    /**
     * SQL: {@code field NOT BETWEEN low AND high}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Specification<T> notBetween(V low, V high) {
        Objects.requireNonNull(low, "low must not be null");
        Objects.requireNonNull(high, "high must not be null");
        validateRangeBounds(low, high);
        return Specification.of(candidate -> {
            V fieldValue = extractor.apply(candidate);
            if (fieldValue == null) return false;
            Comparable comp = (Comparable) fieldValue;
            return comp.compareTo(low) < 0 || comp.compareTo(high) > 0;
        }, fieldName + " NOT BETWEEN " + formatValue(low) + " AND " + formatValue(high));
    }

    // ==================== Internal Helpers ====================

    /**
     * Converts a SQL LIKE pattern to a Java {@link Pattern}.
     * <ul>
     *   <li>{@code %} → {@code .*} (any sequence of characters)</li>
     *   <li>{@code _} → {@code .}  (any single character)</li>
     *   <li>Other regex meta-characters are escaped</li>
     * </ul>
     */
    private static Pattern likeToRegex(String likePattern, boolean ignoreCase) {
        StringBuilder regex = new StringBuilder("^");
        for (int i = 0; i < likePattern.length(); i++) {
            char c = likePattern.charAt(i);
            switch (c) {
                case '%':
                    regex.append(".*");
                    break;
                case '_':
                    regex.append('.');
                    break;
                default:
                    if ("\\.[]{}()*+?^$|".indexOf(c) >= 0) {
                        regex.append('\\');
                    }
                    regex.append(c);
                    break;
            }
        }
        regex.append('$');
        int flags = ignoreCase ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0;
        return Pattern.compile(regex.toString(), flags);
    }

    private static void validateInValues(Collection<?> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (values.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("values must not contain null elements");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void validateRangeBounds(Object low, Object high) {
        try {
            if (((Comparable) low).compareTo(high) > 0) {
                throw new IllegalArgumentException("low must be less than or equal to high");
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("low and high must be mutually comparable", e);
        }
    }

    private static String formatValue(Object value) {
        if (value == null) return "NULL";
        if (value instanceof Boolean) return ((Boolean) value) ? "1" : "0";
        if (value instanceof LocalDate || value instanceof LocalDateTime) {
            return "'" + value + "'";
        }
        if (value instanceof String) return "'" + escapeSqlString((String) value) + "'";
        return value.toString();
    }

    private static String escapeSqlString(String value) {
        return value.replace("'", "''");
    }

    private static String formatValues(Collection<?> values) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Object v : values) {
            joiner.add(formatValue(v));
        }
        return joiner.toString();
    }
}

