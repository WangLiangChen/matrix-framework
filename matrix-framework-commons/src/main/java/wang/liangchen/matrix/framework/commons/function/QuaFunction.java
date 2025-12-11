package wang.liangchen.matrix.framework.commons.function;

import java.util.Objects;
import java.util.function.Function;

public interface QuaFunction<T, U, V, W, R> {
    R apply(T t, U u, V v, W w);

    default <X> QuaFunction<T, U, V, W, X> andThen(Function<? super R, ? extends X> after) {
        Objects.requireNonNull(after);
        return (t, u, v, w) -> after.apply(apply(t, u, v, w));
    }
}
