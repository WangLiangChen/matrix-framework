package wang.liangchen.matrix.framework.data.mybatis.handler;

import org.apache.ibatis.type.BaseTypeHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author LiangChen.Wang 2024/10/17 15:14
 */
public abstract class AbstractObjectTypeHandler extends BaseTypeHandler<Object> {
    private final Class<?> resultClass;
    private final Type resultType;
    private final Class<?>[] actualClasses;

    public AbstractObjectTypeHandler(Class<?> resultClass) {
        this.resultClass = resultClass;
        this.resultType = null;
        this.actualClasses = null;
    }

    public AbstractObjectTypeHandler(Class<?> resultClass, Type resultType) {
        this.resultClass = resultClass;
        this.resultType = resultType;
        if (!(resultType instanceof ParameterizedType)) {
            this.actualClasses = null;
            return;
        }
        ParameterizedType parameterizedType = (ParameterizedType) resultType;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        this.actualClasses = new Class<?>[actualTypeArguments.length];
        for (int i = 0; i < actualTypeArguments.length; i++) {
            this.actualClasses[i] = (Class<?>) actualTypeArguments[i];
        }
    }

    public Class<?> getResultClass() {
        return resultClass;
    }

    public Type getResultType() {
        return resultType;
    }

    public Class<?>[] getActualClasses() {
        return actualClasses;
    }
}
