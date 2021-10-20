package liangchen.wang.matrix.framework.data.query;

/**
 * @author .LiangChen.Wang
 */
public enum AndOr {
    //
    AND(" and "), OR(" or ");
    private final String andOr;

    AndOr(String andOr) {
        this.andOr = andOr;
    }

    public String getAndOr() {
        return andOr;
    }
}
