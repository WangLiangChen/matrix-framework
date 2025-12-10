package wang.liangchen.matrix.framework.commons.function.supplier;

import wang.liangchen.matrix.framework.commons.function.SerializableFunctionalInterface;

import java.util.function.Supplier;

public interface SerializableSupplier<T> extends Supplier<T>, SerializableFunctionalInterface<T> {
}
