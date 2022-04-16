package wang.liangchen.matrix.framework.commons.type;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Liangchen.Wang 2021-09-30 15:22
 */
public enum ClassUtil {
    /**
     * instance
     */
    INSTANCE;

    public Class<?> forName(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new MatrixErrorException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T instantiate(String className) {
        return (T) instantiate(forName(className));
    }

    public <T> T instantiate(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    public Set<Field> declaredFields(final Class<?> clazz, Predicate<Field> filter) {
        Field[] fields = clazz.getDeclaredFields();
        Stream<Field> stream = Arrays.stream(fields);
        if (null == filter) {
            return stream.collect(Collectors.toSet());
        }
        return stream.filter(filter).collect(Collectors.toSet());
    }

    public Set<Field> declaredFields(final Class<?> clazz, Predicate<Field> filter, boolean containSuperFields) {
        if (!containSuperFields) {
            return declaredFields(clazz, filter);
        }
        Class<?> currentClass = clazz;
        Set<Field> fields = new HashSet<>();
        while (Object.class != currentClass) {
            fields.addAll(declaredFields(currentClass, filter));
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

    public Set<Field> declaredFields(final Class<?> clazz) {
        return declaredFields(clazz, null);
    }

    public Set<Field> declaredFields(final Class<?> clazz, boolean containSuperFields) {
        return declaredFields(clazz, null, containSuperFields);
    }


}
