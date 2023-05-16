package wang.liangchen.matrix.framework.data.dao.criteria;

/**
 * @author Liangchen.Wang 2023-02-15 14:56
 */
public enum AndOr {
    and(" and "), or(" or ");

    private final String symbol;

    AndOr(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}

