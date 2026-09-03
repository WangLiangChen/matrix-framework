package wang.liangchen.matrix.framework.ddd.processor;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-11-27 22:12
 */
final class BeanPropertyProcessor implements IPropertyProcessor {
    private final PropertyDescriptor descriptor;
    private final Method readMethod;
    private final Method writeMethod;
    private final Field field;
    private final Set<Annotation> annotations;

    public BeanPropertyProcessor(PropertyDescriptor descriptor, Field field) {
        this.descriptor = descriptor;
        this.readMethod = descriptor.getReadMethod();
        this.writeMethod = descriptor.getWriteMethod();
        this.field = field;
        this.annotations = field != null ? Set.of(field.getAnnotations()) : Set.of();
    }


    @Override
    public String getName() {
        return descriptor.getName();
    }

    @Override
    public Class<?> getType() {
        return descriptor.getPropertyType();
    }

    @Override
    public Type getGenericType() {
        if (field != null) {
            return field.getGenericType();
        }
        if (readMethod != null) {
            return readMethod.getGenericReturnType();
        }
        return getType();
    }

    @Override
    public Object getValue(Object target) {
        if (readMethod == null) {
            throw new IllegalStateException("Property " + getName() + " has no getter");
        }
        try {
            return readMethod.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read property: " + getName(), e.getCause());
        }
    }

    @Override
    public void setValue(Object target, Object value) {
        if (writeMethod == null) {
            throw new IllegalStateException("Property " + getName() + " has no setter");
        }
        try {
            writeMethod.invoke(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write property: " + getName(), e.getCause());
        }
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public Class<?> getDeclaringClass() {
        if (field != null) {
            return field.getDeclaringClass();
        }
        if (readMethod != null) {
            return readMethod.getDeclaringClass();
        }
        if (writeMethod != null) {
            return writeMethod.getDeclaringClass();
        }
        throw new IllegalStateException("Property " + getName() + " has no declaring class");
    }
}