package wang.liangchen.matrix.framework.commons.function;

import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.object.BeanUtil;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Liangchen.Wang 2022-04-15 10:15
 */
public enum LambdaUtil {
    /**
     * instance
     */
    INSTANCE;

    public String getReferencedFieldName(SerializableFunctionInterface serializableFunctionInterface) {
        String methodName = serializedLambda(serializableFunctionInterface).getImplMethodName();
        return BeanUtil.INSTANCE.resolveFieldName(methodName);
    }

    public String getReferencedMethodName(SerializableFunctionInterface serializableFunctionInterface) {
        return serializedLambda(serializableFunctionInterface).getImplMethodName();
    }

    public SerializedLambda serializedLambda(SerializableFunctionInterface serializableFunctionInterface) {
        try {
            Method writeReplace = serializableFunctionInterface.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(serializableFunctionInterface, CollectionUtil.INSTANCE.emptyArray());
            return serializedLambda;
        } catch (NoSuchMethodException e) {
            throw new MatrixErrorException("functionInterface is not a serializable FunctionInterface");
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new MatrixErrorException("functionInterface is not a serializable FunctionInterface");
        }
    }
}
