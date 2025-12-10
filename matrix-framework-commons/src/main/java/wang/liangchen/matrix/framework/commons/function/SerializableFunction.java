package wang.liangchen.matrix.framework.commons.function;

import java.util.function.Function;

/**
 * @author Liangchen.Wang 2021-08-23 10:55
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, SerializableFunctionalInterface<T> {
}
