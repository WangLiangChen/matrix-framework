package wang.liangchen.matrix.framework.commons.function;

import java.util.Objects;

/**
 * @author Liangchen.Wang 2021-08-23 10:55
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends SerializableFunctionInterface {
    R apply(T t);


    default <V> SerializableFunction<V, R> compose(SerializableFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }


    default <V> SerializableFunction<T, V> andThen(SerializableFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }

    static <T> SerializableFunction<T, T> identity() {
        return t -> t;
    }
}
