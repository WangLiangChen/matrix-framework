package wang.liangchen.matrix.framework.spring.data.criteria;

/**
 * @author LiangChen.Wang 2024/11/6 18:22
 */
abstract class AbstractCriteriaResolver {
    private final AndOr andOr;

    AbstractCriteriaResolver(AndOr andOr) {
        this.andOr = andOr;
    }

    public AndOr getAndOr() {
        return andOr;
    }
}
