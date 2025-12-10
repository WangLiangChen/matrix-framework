package wang.liangchen.matrix.framework.commons.function;

import java.util.Objects;
import java.util.function.Function;

public interface QuaFunction<T, U, V, S, R> {
    R apply(T t, U u, V v, S s);

    default <W> QuaFunction<T, U, V, S, W> andThen(Function<? super R, ? extends W> after) {
        Objects.requireNonNull(after);
        return (t, u, v, s) -> after.apply(apply(t, u, v, s));
    }
}
