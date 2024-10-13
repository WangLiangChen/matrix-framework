package wang.liangchen.matrix.framework.commons.annotation;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author Liangchen.Wang 2023-06-17 18:14
 */
public enum AnnotationUtil {
    INSTANCE;

    public Object replaceAnnotationValue(Annotation annotation, String key, Object newValue) {
        InvocationHandler handler = Proxy.getInvocationHandler(annotation);
        try {
            Field field = handler.getClass().getDeclaredField("memberValues");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Object> memberValues = (Map<String, Object>) field.get(handler);
            Object oldValue = memberValues.get(key);
            memberValues.put(key, newValue);
            return oldValue;
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }
}
