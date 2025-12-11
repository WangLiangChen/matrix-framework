package wang.liangchen.matrix.framework.commons.function;

/**
 * @author Liangchen.Wang 2021-08-23 10:55
 */
@FunctionalInterface
public interface SerializableQuaFunction<T, U, V, W, R> extends QuaFunction<T, U, V, W, R>, SerializableFunctionalInterface<T> {
}
