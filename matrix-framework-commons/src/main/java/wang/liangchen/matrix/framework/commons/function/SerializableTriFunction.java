package wang.liangchen.matrix.framework.commons.function;

/**
 * @author Liangchen.Wang 2021-08-23 10:55
 */
@FunctionalInterface
public interface SerializableTriFunction<T, U, V, R> extends TriFunction<T, U, V, R>, SerializableFunctionalInterface<T> {

}
