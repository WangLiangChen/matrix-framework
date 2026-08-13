package wang.liangchen.matrix.framework.ddd.specification;

/**
 * OR composite specification: satisfied when at least one of left or right is satisfied.
 *
 * @author Liangchen.Wang
 */
final class OrSpecification<T> extends Specification<T> {
    private final Specification<T> left;
    private final Specification<T> right;

    OrSpecification(Specification<T> left, Specification<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        return left.isSatisfiedBy(candidate) || right.isSatisfiedBy(candidate);
    }

    @Override
    public String toString() {
        return "(" + left + " OR " + right + ")";
    }

    @Override
    public String toSql() {
        return "(" + left.toSql() + " OR " + right.toSql() + ")";
    }
}

