package wang.liangchen.matrix.framework.commons.type;


import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author LiangChen.Wang
 */
public abstract class TypeToken<T> {
    private final Type type;

    public TypeToken() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new MatrixWarnException("No generics found!");
        }
        ParameterizedType type = (ParameterizedType) superclass;
        this.type = type.getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
