package wang.liangchen.matrix.framework.commons.function;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.object.JavaBeanUtil;

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

    public String getReferencedFieldName(SerializableFunctionalInterface serializableFunctionalInterface) {
        String methodName = serializedLambda(serializableFunctionalInterface).getImplMethodName();
        return JavaBeanUtil.INSTANCE.resolveFieldName(methodName);
    }

    public String getReferencedMethodName(SerializableFunctionalInterface serializableFunctionalInterface) {
        return serializedLambda(serializableFunctionalInterface).getImplMethodName();
    }

    public String getReferencedClassName(SerializableFunctionalInterface serializableFunctionalInterface) {
        return serializedLambda(serializableFunctionalInterface).getImplClass().replace('/', '.');
    }

    public SerializedLambda serializedLambda(SerializableFunctionalInterface serializableFunctionalInterface) {
        try {
            Method writeReplace = serializableFunctionalInterface.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            return (SerializedLambda) writeReplace.invoke(serializableFunctionalInterface);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new MatrixErrorException("functionInterface is not a serializable FunctionInterface");
        }
    }
}
