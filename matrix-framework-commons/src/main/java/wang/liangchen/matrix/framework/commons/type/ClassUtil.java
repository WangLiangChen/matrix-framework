package wang.liangchen.matrix.framework.commons.type;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private static final Map<String, MethodAccess> METHOD_ACCESS_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> defaultValue = new HashMap<Class<?>, Object>() {{
        put(Long.class, 0L);
        put(Integer.class, 0);
        put(Short.class, (short) 0);
        put(Byte.class, (byte) 0);
        put(Double.class, 0d);
        put(Float.class, 0f);
        put(Boolean.class, Boolean.FALSE);
        put(BigDecimal.class, new BigDecimal(0));
        put(Timestamp.class, new Timestamp(System.currentTimeMillis()));
        put(String.class, Symbol.BLANK.getSymbol());
        put(LocalDate.class, LocalDate.now());
        put(LocalDateTime.class, LocalDateTime.now());
        put(Date.class, new Date());
    }};

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

    public void initializeFields(Object target) {
        MethodAccess methodAccess = methodAccess(target.getClass());
        String[] methodNames = methodAccess.getMethodNames();
        Class<?>[] returnTypes = methodAccess.getReturnTypes();
        for (int i = 0; i < methodNames.length; i++) {
            String methodName = methodNames[i];
            if (!methodName.startsWith(Symbol.GETTER_PREFIX.getSymbol())) {
                continue;
            }
            Object object = methodAccess.invoke(target, methodName);
            if (null != object) {
                continue;
            }
            Class<?> returnType = returnTypes[i];
            object = defaultValue.get(returnType);
            if (null == object) {
                continue;
            }
            methodName = methodName.replace(Symbol.GETTER_PREFIX.getSymbol(), Symbol.SETTER_PREFIX.getSymbol());
            methodAccess.invoke(target, methodName, object);
        }
    }

    public List<Field> declaredFields(final Class<?> clazz, Predicate<Field> fieldFilter) {
        Field[] fields = clazz.getDeclaredFields();
        Stream<Field> stream = Arrays.stream(fields);
        if (null == fieldFilter) {
            return stream.collect(Collectors.toList());
        }
        return stream.filter(fieldFilter).collect(Collectors.toList());
    }

    public List<Field> declaredFields(final Class<?> clazz, Predicate<Class<?>> classFilter, Predicate<Field> fieldFilter) {
        List<Class<?>> container = populateSuperClasses(clazz, classFilter);
        List<Field> fields = new ArrayList<>();
        container.forEach(innerClass -> fields.addAll(declaredFields(innerClass, fieldFilter)));
        // 去重
        Iterator<Field> fieldIterator = fields.iterator();
        Set<String> fieldNames = new HashSet<>();
        while (fieldIterator.hasNext()) {
            String name = fieldIterator.next().getName();
            if (fieldNames.contains(name)) {
                fieldIterator.remove();
                break;
            }
            fieldNames.add(name);
        }
        return fields;
    }

    public List<Field> declaredFields(final Class<?> clazz) {
        return declaredFields(clazz, null);
    }

    public <T> ConstructorAccess<T> constructorAccess(Class<T> targetClass) {
        return CONSTRUCTOR_ACCESS_CACHE.computeIfAbsent(targetClass.getName(), key -> ConstructorAccess.get(targetClass));
    }

    public MethodAccess methodAccess(Class<?> targetClass) {
        return METHOD_ACCESS_CACHE.computeIfAbsent(targetClass.getName(), key -> MethodAccess.get(targetClass));
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
