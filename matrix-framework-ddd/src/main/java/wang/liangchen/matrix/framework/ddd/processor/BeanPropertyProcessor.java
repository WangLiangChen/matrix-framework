package wang.liangchen.matrix.framework.ddd.processor;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-11-27 22:12
 */
public class BeanPropertyProcessor implements IPropertyProcessor {
    private final PropertyDescriptor descriptor;
    private final Method readMethod;
    private final Method writeMethod;
    private final Field field;

    public BeanPropertyProcessor(PropertyDescriptor descriptor) {
        this.descriptor = descriptor;
        this.readMethod = descriptor.getReadMethod();
        this.writeMethod = descriptor.getWriteMethod();
        this.field = resolveField();
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
            throw new RuntimeException("Failed to read property: " + getName(), e);
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
            throw new RuntimeException("Failed to write property: " + getName(), e);
        }
    }

    @Override
    public Set<Annotation> getAnnotations() {
        if (field != null) {
            return new HashSet<>(Arrays.asList(field.getAnnotations()));
        }
        return new HashSet<>();
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

    @Override
    public int getModifiers() {
        if (field != null) {
            return field.getModifiers();
        }
        return 0;
    }

    @Override
    public boolean isReadable() {
        return readMethod != null;
    }

    @Override
    public boolean isWritable() {
        return writeMethod != null;
    }

    private Field resolveField() {
        Method method = readMethod != null ? readMethod : writeMethod;
        if (method == null) {
            return null;
        }
        Class<?> declaringClass = method.getDeclaringClass();
        while (declaringClass != null && declaringClass != Object.class) {
            try {
                return declaringClass.getDeclaredField(descriptor.getName());
            } catch (NoSuchFieldException e) {
                declaringClass = declaringClass.getSuperclass();
            }
        }
        return null;
    }
}