package wang.liangchen.matrix.framework.commons.function;

import java.util.Objects;

/**
 * @author Liangchen.Wang 2021-08-23 10:55
 */
@FunctionalInterface
public interface SerializableConsumer<T> extends SerializableFunctionInterface {
    void accept(T t);

    default SerializableConsumer<T> andThen(SerializableConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
