package wang.liangchen.matrix.framework.commons.function.consumer;

import wang.liangchen.matrix.framework.commons.function.SerializableFunctionalInterface;

/**
 * @author Liangchen.Wang 2021-08-23 10:55
 */
@FunctionalInterface
public interface SerializableTriConsumer<T, U, V> extends TriConsumer<T, U, V>, SerializableFunctionalInterface<T> {
}
