package wang.liangchen.matrix.framework.commons.function.consumer;

import wang.liangchen.matrix.framework.commons.function.SerializableFunctionalInterface;

import java.util.function.Consumer;

/**
 * @author Liangchen.Wang 2021-08-23 10:55
 */
@FunctionalInterface
public interface SerializableConsumer<T> extends Consumer<T>, SerializableFunctionalInterface<T> {
}
