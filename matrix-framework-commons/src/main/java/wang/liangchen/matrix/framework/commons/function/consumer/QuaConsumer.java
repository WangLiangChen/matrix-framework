package wang.liangchen.matrix.framework.commons.function.consumer;

import java.util.Objects;

/**
 * @author Liangchen.Wang 2021-08-23 10:55
 */
@FunctionalInterface
public interface QuaConsumer<T, U, V, W> {
    void accept(T t, U u, V v, W w);

    default QuaConsumer<T, U, V, W> andThen(QuaConsumer<? super T, ? super U, ? super V, ? super W> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w) -> {
            accept(t, u, v, w);
            after.accept(t, u, v, w);
        };
    }
}
