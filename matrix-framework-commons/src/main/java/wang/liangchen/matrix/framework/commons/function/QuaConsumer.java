package wang.liangchen.matrix.framework.commons.function;

import java.util.Objects;

/**
 * @author Liangchen.Wang 2021-08-23 10:55
 */
@FunctionalInterface
public interface QuaConsumer<T, U, V, S> {
    void accept(T t, U u, V v, S s);

    default QuaConsumer<T, U, V, S> andThen(QuaConsumer<? super T, ? super U, ? super V, ? super S> after) {
        Objects.requireNonNull(after);
        return (t, u, v, s) -> {
            accept(t, u, v, s);
            after.accept(t, u, v, s);
        };
    }
}
