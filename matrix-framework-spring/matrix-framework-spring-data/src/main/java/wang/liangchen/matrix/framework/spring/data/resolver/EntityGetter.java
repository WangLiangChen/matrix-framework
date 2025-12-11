package wang.liangchen.matrix.framework.spring.data.resolver;

import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.commons.function.SerializableFunction;
import wang.liangchen.matrix.framework.spring.data.entity.RootEntity;


/**
 * @author Liangchen.Wang 2022-04-15 22:06
 */
@FunctionalInterface
public interface EntityGetter<T extends RootEntity, R> extends SerializableFunction<T, R> {

    default String getFieldName() {
        return LambdaUtil.INSTANCE.getReferencedFieldName(this);
    }
}
