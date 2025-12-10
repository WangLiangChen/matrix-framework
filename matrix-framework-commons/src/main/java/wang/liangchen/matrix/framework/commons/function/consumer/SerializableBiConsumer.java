package wang.liangchen.matrix.framework.commons.function.consumer;

import wang.liangchen.matrix.framework.commons.function.SerializableFunctionalInterface;

import java.util.function.BiConsumer;

/**
 * @author Liangchen.Wang 2021-08-23 10:55
 */
@FunctionalInterface
public interface SerializableBiConsumer<T, U> extends BiConsumer<T, U>, SerializableFunctionalInterface<T> {
}
