package wang.liangchen.matrix.framework.commons.function.consumer;

import wang.liangchen.matrix.framework.commons.function.SerializableFunctionalInterface;

/**
 * @author Liangchen.Wang 2021-08-23 10:55
 */
@FunctionalInterface
public interface SerializableQuaConsumer<T, U, V, S> extends QuaConsumer<T, U, V, S>, SerializableFunctionalInterface<T> {
}
