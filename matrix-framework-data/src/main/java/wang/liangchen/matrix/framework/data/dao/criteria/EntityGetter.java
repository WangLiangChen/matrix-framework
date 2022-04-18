package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.function.SerializableFunctionInterface;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

/**
 * @author Liangchen.Wang 2022-04-15 22:06
 */
@FunctionalInterface
public interface EntityGetter<T extends RootEntity> extends SerializableFunctionInterface {
    void accept(T t);
}
