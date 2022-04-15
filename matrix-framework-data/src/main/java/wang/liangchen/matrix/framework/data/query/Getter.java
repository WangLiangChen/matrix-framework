package wang.liangchen.matrix.framework.data.query;

import wang.liangchen.matrix.framework.commons.function.SerializableFunctionInterface;

/**
 * @author Liangchen.Wang 2022-04-15 22:06
 */
@FunctionalInterface
public interface Getter<T> extends SerializableFunctionInterface {
    void accept(T t);
}
