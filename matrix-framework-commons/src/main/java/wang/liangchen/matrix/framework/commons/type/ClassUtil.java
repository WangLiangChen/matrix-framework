package wang.liangchen.matrix.framework.commons.type;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Liangchen.Wang 2021-09-30 15:22
 */
@SuppressWarnings("unchecked")
public enum ClassUtil {
    /**
     * instance
     */
    INSTANCE;
    private static final Map<String, ConstructorAccess> CONSTRUCTOR_ACCESS_CACHE = new ConcurrentHashMap<>();

    public Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new MatrixErrorException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T instantiate(String className) {
        return (T) instantiate(forName(className));
    }

    public <T> T instantiate(Class<T> clazz) {
        return constructorAccess(clazz).newInstance();
    }

    public Set<Field> declaredFields(final Class<?> clazz, Predicate<Field> fieldFilter) {
        Field[] fields = clazz.getDeclaredFields();
        Stream<Field> stream = Arrays.stream(fields);
        if (null == fieldFilter) {
            return stream.collect(Collectors.toSet());
        }
        return stream.filter(fieldFilter).collect(Collectors.toSet());
    }

    public Set<Field> declaredFields(final Class<?> clazz, Predicate<Class<?>> classFilter, Predicate<Field> fieldFilter) {
        List<Class<?>> container = populateSuperClasses(clazz, classFilter);
        Set<Field> fields = new HashSet<>();
        container.forEach(innerClass -> fields.addAll(declaredFields(clazz, fieldFilter)));
        return fields;
    }

    public Set<Field> declaredFields(final Class<?> clazz) {
        return declaredFields(clazz, null);
    }

    private <T> ConstructorAccess<T> constructorAccess(Class<T> targetClass) {
        return CONSTRUCTOR_ACCESS_CACHE.computeIfAbsent(targetClass.getName(), key -> {
            ConstructorAccess<T> constructorAccess = ConstructorAccess.get(targetClass);
            constructorAccess.newInstance();
            return constructorAccess;
        });
    }

    private List<Class<?>> populateSuperClasses(final Class<?> clazz, Predicate<Class<?>> classFilter) {
        List<Class<?>> container = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (Object.class != currentClass) {
            container.add(currentClass);
            currentClass = currentClass.getSuperclass();
        }
        if (null == classFilter) {
            return container;
        }
        return container.stream().filter(classFilter).collect(Collectors.toList());
    }

}
