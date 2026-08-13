package wang.liangchen.matrix.framework.ddd.specification;

import java.util.Objects;
import java.util.function.Function;

/**
 * Typed field metadata used by {@link SpecificationParser}.
 * <p>
 * Carries the logical field name, Java value type, and extractor function so that SQL-like literals can
 * be converted to the correct runtime type before building a {@link Specification}.
 * </p>
 *
 * <pre>{@code
 * SpecificationField<User, Boolean> active =
     *         SpecificationField.of("active", Boolean.class, User::isActive);
 * Specification<User> spec = SpecificationParser.parse("active = 1", active);
 * }</pre>
 *
 * @param <T> candidate object type
 * @param <V> field value type
 */
public final class SpecificationField<T, V> {

    private final String name;
    private final Class<V> type;
    private final Function<T, V> extractor;

    private SpecificationField(String name, Class<V> type, Function<T, V> extractor) {
        this.name = requireName(name);
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.extractor = Objects.requireNonNull(extractor, "extractor must not be null");
    }

    public static <T, V> SpecificationField<T, V> of(String name, Class<V> type, Function<T, V> extractor) {
        return new SpecificationField<>(name, type, extractor);
    }

    public String name() {
        return name;
    }

    public Class<V> type() {
        return type;
    }

    public Function<T, V> extractor() {
        return extractor;
    }

    private static String requireName(String name) {
        Objects.requireNonNull(name, "name must not be null");
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return name;
    }
}


