package wang.liangchen.matrix.framework.commons.function;

import java.util.function.BiFunction;

/**
 * @author Liangchen.Wang 2021-08-23 10:55
 */
@FunctionalInterface
public interface SerializableBiFunction<T, U, R> extends BiFunction<T, U, R>, SerializableFunctionalInterface<T> {
}
