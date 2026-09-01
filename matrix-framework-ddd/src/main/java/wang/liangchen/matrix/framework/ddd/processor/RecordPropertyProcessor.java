package wang.liangchen.matrix.framework.ddd.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-11-27 22:12
 */
public class RecordPropertyProcessor implements IPropertyProcessor {
    private final RecordComponent component;
    private final Method accessor;

    public RecordPropertyProcessor(RecordComponent component, Method accessor) {
        this.component = component;
        this.accessor = accessor;
    }

    @Override
    public String getName() {
        return component.getName();
    }

    @Override
    public Class<?> getType() {
        return component.getType();
    }

    @Override
    public Type getGenericType() {
        return component.getGenericType();
    }

    @Override
    public Object getValue(Object target) {
        try {
            return accessor.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(Object target, Object value) {
        throw new UnsupportedOperationException("Record components are immutable");
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return new HashSet<>(Arrays.asList(component.getAnnotations()));
    }

    @Override
    public Class<?> getDeclaringClass() {
        return accessor.getDeclaringClass();
    }

    @Override
    public int getModifiers() {
        try {
            return accessor.getDeclaringClass().getDeclaredField(component.getName()).getModifiers();
        } catch (NoSuchFieldException e) {
            return 0;
        }
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public boolean isWritable() {
        return false;
    }
}