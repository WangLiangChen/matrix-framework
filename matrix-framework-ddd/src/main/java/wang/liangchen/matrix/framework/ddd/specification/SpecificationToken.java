package wang.liangchen.matrix.framework.ddd.specification;

/**
 * Token produced by {@link SpecificationTokenizer} for SQL WHERE clause parsing.
 *
 * @author Liangchen.Wang
 */
final class SpecificationToken {

    enum TokenType {
        // Literals & identifiers
        IDENTIFIER, STRING_LITERAL, NUMBER_LITERAL,
        // SQL keywords
        AND, OR, NOT, BETWEEN, IN, IS, LIKE, UPPER, NULL, TRUE, FALSE,
        // Comparison operators
        EQ, NEQ, GT, GTE, LT, LTE,
        // Punctuation
        LPAREN, RPAREN, COMMA,
        // End of input
        EOF
    }

    private final TokenType type;
    private final String value;
    private final int position;

    SpecificationToken(TokenType type, String value, int position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    TokenType type() {
        return type;
    }

    String value() {
        return value;
    }

    int position() {
        return position;
    }

    @Override
    public String toString() {
        return type + "(" + value + ")@" + position;
    }
}

