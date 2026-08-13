package wang.liangchen.matrix.framework.ddd.specification;

/**
 * NOT decorator specification: satisfied when the wrapped specification is NOT satisfied.
 *
 * @author Liangchen.Wang
 */
final class NotSpecification<T> extends Specification<T> {
    private final Specification<T> left;

    NotSpecification(Specification<T> left) {
        this.left = left;
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        return !left.isSatisfiedBy(candidate);
    }

    @Override
    public String toString() {
        return "(NOT " + left + ")";
    }

    @Override
    public String toSql() {
        return "(NOT " + left.toSql() + ")";
    }
}

