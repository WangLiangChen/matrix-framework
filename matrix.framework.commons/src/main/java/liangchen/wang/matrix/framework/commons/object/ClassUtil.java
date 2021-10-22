package liangchen.wang.matrix.framework.commons.object;

import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
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

    @SuppressWarnings("unchecked")
    public <T> T instantiate(String className) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(className);
            return instantiate(clazz);
        } catch (ClassNotFoundException e) {
            throw new MatrixErrorException(e);
        }
    }

    public <T> T instantiate(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    public Set<Field> fields(final Class<?> clazz) {
        return fields(clazz, null);
    }

    public Set<Field> fields(final Class<?> clazz, Predicate<Field> filter) {
        Field[] fields = clazz.getDeclaredFields();
        Stream<Field> stream = Arrays.stream(fields);
        if (null == filter) {
            return stream.collect(Collectors.toSet());
        }
        return stream.filter(filter).collect(Collectors.toSet());
    }

    public Set<Field> fields(final Class<?> clazz, boolean allFields) {
        return fields(clazz, allFields, null);
    }

    public Set<Field> fields(final Class<?> clazz, boolean allFields, Predicate<Field> filter) {
        if (!allFields) {
            return fields(clazz, filter);
        }

        Class<?> localClass = clazz;
        Set<Field> fields = new HashSet<>();
        while (Object.class != localClass) {
            fields.addAll(fields(localClass, filter));
            localClass = localClass.getSuperclass();
        }
        return fields;
    }
}
