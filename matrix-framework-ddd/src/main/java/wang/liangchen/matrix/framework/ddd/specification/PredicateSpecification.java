package wang.liangchen.matrix.framework.ddd.specification;

import java.util.function.Predicate;

/**
 * Specification wrapping an external {@link Predicate}.
 * <p>
 * Bridge between the {@code Predicate} functional interface and the {@code Specification} abstraction.
 * </p>
 *
 * @author Liangchen.Wang
 */
final class PredicateSpecification<T> extends Specification<T> {
    private final Predicate<T> predicate;
    private final String description;

    PredicateSpecification(Predicate<T> predicate, String description) {
        this.predicate = predicate;
        this.description = description;
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        return predicate.test(candidate);
    }

    @Override
    public String toString() {
        return description != null ? description : predicate.toString();
    }

    @Override
    public String toSql() {
        if (description == null) {
            throw new IllegalStateException("Specification created from an arbitrary Predicate has no SQL representation");
        }
        return description;
    }
}

