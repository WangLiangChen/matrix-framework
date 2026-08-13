package wang.liangchen.matrix.framework.ddd.specification;

import wang.liangchen.matrix.framework.ddd.specification.SpecificationToken.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lexer that tokenizes a T-SQL WHERE clause string into {@link SpecificationToken}s.
 * <p>
 * SQL keywords are case-insensitive. String literals use single quotes with {@code ''} escape.
 * Numeric literals support integers and decimals.
 * </p>
 *
 * @author Liangchen.Wang
 */
final class SpecificationTokenizer {

    private static final Map<String, TokenType> KEYWORDS = buildKeywords();

    private final String input;
    private int pos;

    private SpecificationTokenizer(String input) {
        this.input = input;
        this.pos = 0;
    }

    /**
     * Tokenizes the given SQL WHERE clause string.
     *
     * @param sql the SQL condition string
     * @return list of tokens ending with {@link TokenType#EOF}
     * @throws IllegalArgumentException on unrecognized characters or unterminated strings
     */
    static List<SpecificationToken> tokenize(String sql) {
        return new SpecificationTokenizer(sql).doTokenize();
    }

    private List<SpecificationToken> doTokenize() {
        List<SpecificationToken> tokens = new ArrayList<>();
        while (pos < input.length()) {
            skipWhitespace();
            if (pos >= input.length()) {
                break;
            }
            char c = input.charAt(pos);

            if (c == '\'') {
                tokens.add(readStringLiteral());
            } else if (c == '(') {
                tokens.add(new SpecificationToken(TokenType.LPAREN, "(", pos++));
            } else if (c == ')') {
                tokens.add(new SpecificationToken(TokenType.RPAREN, ")", pos++));
            } else if (c == ',') {
                tokens.add(new SpecificationToken(TokenType.COMMA, ",", pos++));
            } else if (c == '=') {
                tokens.add(new SpecificationToken(TokenType.EQ, "=", pos++));
            } else if (c == '<') {
                tokens.add(readLessThanOrNeq());
            } else if (c == '>') {
                tokens.add(readGreaterThan());
            } else if (c == '!' && pos + 1 < input.length() && input.charAt(pos + 1) == '=') {
                tokens.add(new SpecificationToken(TokenType.NEQ, "!=", pos));
                pos += 2;
            } else if (Character.isDigit(c) || (c == '-' && pos + 1 < input.length() && Character.isDigit(input.charAt(pos + 1)))) {
                tokens.add(readNumberLiteral());
            } else if (isIdentifierStart(c)) {
                tokens.add(readIdentifierOrKeyword());
            } else {
                throw new IllegalArgumentException(
                        "Unexpected character '" + c + "' at position " + pos + " in: " + input);
            }
        }
        tokens.add(new SpecificationToken(TokenType.EOF, "", pos));
        return tokens;
    }

    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }

    private SpecificationToken readStringLiteral() {
        int start = pos;
        pos++; // skip opening quote
        StringBuilder sb = new StringBuilder();
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '\'') {
                if (pos + 1 < input.length() && input.charAt(pos + 1) == '\'') {
                    sb.append('\'');
                    pos += 2; // escaped quote
                } else {
                    pos++; // closing quote
                    return new SpecificationToken(TokenType.STRING_LITERAL, sb.toString(), start);
                }
            } else {
                sb.append(c);
                pos++;
            }
        }
        throw new IllegalArgumentException("Unterminated string literal starting at position " + start + " in: " + input);
    }

    private SpecificationToken readNumberLiteral() {
        int start = pos;
        if (input.charAt(pos) == '-') {
            pos++;
        }
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            pos++;
        }
        if (pos < input.length() && input.charAt(pos) == '.') {
            pos++;
            while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                pos++;
            }
        }
        return new SpecificationToken(TokenType.NUMBER_LITERAL, input.substring(start, pos), start);
    }

    private SpecificationToken readLessThanOrNeq() {
        int start = pos;
        pos++;
        if (pos < input.length()) {
            if (input.charAt(pos) == '=') {
                pos++;
                return new SpecificationToken(TokenType.LTE, "<=", start);
            }
            if (input.charAt(pos) == '>') {
                pos++;
                return new SpecificationToken(TokenType.NEQ, "<>", start);
            }
        }
        return new SpecificationToken(TokenType.LT, "<", start);
    }

    private SpecificationToken readGreaterThan() {
        int start = pos;
        pos++;
        if (pos < input.length() && input.charAt(pos) == '=') {
            pos++;
            return new SpecificationToken(TokenType.GTE, ">=", start);
        }
        return new SpecificationToken(TokenType.GT, ">", start);
    }

    private SpecificationToken readIdentifierOrKeyword() {
        int start = pos;
        while (pos < input.length() && isIdentifierPart(input.charAt(pos))) {
            pos++;
        }
        String text = input.substring(start, pos);
        TokenType keyword = KEYWORDS.get(text.toUpperCase());
        if (keyword != null) {
            return new SpecificationToken(keyword, text, start);
        }
        return new SpecificationToken(TokenType.IDENTIFIER, text, start);
    }

    private static boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private static boolean isIdentifierPart(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '.';
    }

    private static Map<String, TokenType> buildKeywords() {
        Map<String, TokenType> keywords = new HashMap<>();
        keywords.put("AND", TokenType.AND);
        keywords.put("OR", TokenType.OR);
        keywords.put("NOT", TokenType.NOT);
        keywords.put("BETWEEN", TokenType.BETWEEN);
        keywords.put("IN", TokenType.IN);
        keywords.put("IS", TokenType.IS);
        keywords.put("LIKE", TokenType.LIKE);
        keywords.put("UPPER", TokenType.UPPER);
        keywords.put("NULL", TokenType.NULL);
        keywords.put("TRUE", TokenType.TRUE);
        keywords.put("FALSE", TokenType.FALSE);
        return keywords;
    }
}

