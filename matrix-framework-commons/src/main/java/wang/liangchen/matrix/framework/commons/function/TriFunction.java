package wang.liangchen.matrix.framework.commons.function;

import java.util.Objects;
import java.util.function.Function;

public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);

    default <W> TriFunction<T, U, V, W> andThen(Function<? super R, ? extends W> after) {
        Objects.requireNonNull(after);
        return (t, u, v) -> after.apply(apply(t, u, v));
    }
}
