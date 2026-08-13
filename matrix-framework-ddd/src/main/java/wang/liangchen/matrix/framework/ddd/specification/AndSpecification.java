package wang.liangchen.matrix.framework.ddd.specification;

/**
 * AND composite specification: satisfied when BOTH left and right are satisfied.
 *
 * @author Liangchen.Wang
 */
final class AndSpecification<T> extends Specification<T> {
    private final Specification<T> left;
    private final Specification<T> right;

    AndSpecification(Specification<T> left, Specification<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        return left.isSatisfiedBy(candidate) && right.isSatisfiedBy(candidate);
    }

    @Override
    public String toString() {
        return "(" + left + " AND " + right + ")";
    }

    @Override
    public String toSql() {
        return "(" + left.toSql() + " AND " + right.toSql() + ")";
    }
}

