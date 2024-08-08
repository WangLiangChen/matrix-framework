package wang.liangchen.matrix.framework.commons.type;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
    private static final Map<Class<?>, ConstructorAccess> CONSTRUCTOR_ACCESS_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, MethodAccessor> METHOD_ACCESS_CACHE = new ConcurrentHashMap<>();
    public static final Map<Class<?>, Supplier<?>> TYPE_DEFAULT_VALUE = new HashMap<>() {{
        put(Long.class, () -> 0L);
        put(Integer.class, () -> 0);
        put(Short.class, () -> (short) 0);
        put(Byte.class, () -> (byte) 0);
        put(Double.class, () -> 0d);
        put(Float.class, () -> 0f);
        put(Boolean.class, () -> Boolean.FALSE);
        put(BigDecimal.class, () -> new BigDecimal(0));
        put(String.class, Symbol.BLANK::getSymbol);
        put(Timestamp.class, () -> new Timestamp(System.currentTimeMillis()));
        put(LocalDate.class, LocalDate::now);
        put(LocalDateTime.class, LocalDateTime::now);
        put(Date.class, Date::new);
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

    public void invokeSetter(Object target, String setterMethod, Object... args) {
        MethodAccessor methodAccessor = methodAccessor(target.getClass());
        Map<String, Integer> setters = methodAccessor.getSetters();
        Integer index = setters.get(setterMethod);
        ValidationUtil.INSTANCE.notNull(index, "setter does not exist:{}", setterMethod);
        MethodAccess methodAccess = methodAccessor.getMethodAccess();
        methodAccess.invoke(target, index, args);
    }

    public Object invokeGetter(Object target, String getterMethod) {
        MethodAccessor methodAccessor = methodAccessor(target.getClass());
        Map<String, Integer> getters = methodAccessor.getGetters();
        Integer index = getters.get(getterMethod);
        ValidationUtil.INSTANCE.notNull(index, "getter does not exist:{}", getterMethod);
        MethodAccess methodAccess = methodAccessor.getMethodAccess();
        return methodAccess.invoke(target, index);
    }

    public void initializeFields(Object target) {
        MethodAccessor methodAccessor = methodAccessor(target.getClass());
        MethodAccess methodAccess = methodAccessor.getMethodAccess();
        Map<String, Integer> getters = methodAccessor.getGetters();
        Map<String, Integer> setters = methodAccessor.getSetters();
        Class[] returnTypes = methodAccess.getReturnTypes();
        getters.forEach((getter, getterIndex) -> {
            Object returnValue = methodAccess.invoke(target, getterIndex);
            if (null != returnValue) {
                return;
            }
            String fieldName = getter.substring(3);
            String setter = StringUtil.INSTANCE.getSetter(fieldName);
            Integer setterIndex = setters.get(setter);
            if (null == setterIndex) {
                return;
            }
            Class<?> returnType = returnTypes[getterIndex];
            Supplier<?> supplier = TYPE_DEFAULT_VALUE.getOrDefault(returnType, () -> {
                try {
                    return instantiate(returnType);
                } catch (Exception e) {
                    return null;
                }
            });
            methodAccess.invoke(target, setterIndex, supplier.get());
        });
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

    public List<Method> declaredMethods(final Class<?> clazz, Predicate<Method> methodFilter) {
        Method[] methods = clazz.getDeclaredMethods();
        Stream<Method> stream = Arrays.stream(methods);
        if (null == methodFilter) {
            return stream.collect(Collectors.toList());
        }
        return stream.filter(methodFilter).collect(Collectors.toList());
    }

    public List<Method> declaredMethods(final Class<?> clazz, Predicate<Class<?>> classFilter, Predicate<Method> methodFilter) {
        List<Class<?>> container = populateSuperClasses(clazz, classFilter);
        List<Method> fields = new ArrayList<>();
        container.forEach(innerClass -> fields.addAll(declaredMethods(innerClass, methodFilter)));
        // 去重
        Iterator<Method> fieldIterator = fields.iterator();
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



    public List<Method> declaredMethods(final Class<?> clazz) {
        return declaredMethods(clazz, null);
    }

    public <T> ConstructorAccess<T> constructorAccess(Class<T> targetClass) {
        return CONSTRUCTOR_ACCESS_CACHE.computeIfAbsent(targetClass, key -> ConstructorAccess.get(targetClass));
    }

    public MethodAccessor methodAccessor(Class<?> targetClass) {
        return METHOD_ACCESS_CACHE.computeIfAbsent(targetClass, key -> new MethodAccessor(MethodAccess.get(targetClass)));
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

    public static class MethodAccessor {
        private final MethodAccess methodAccess;
        private final Map<String, Integer> getters;
        private final Map<String, Integer> setters;

        public MethodAccessor(MethodAccess methodAccess) {
            this.methodAccess = methodAccess;
            getters = new HashMap<>();
            setters = new HashMap<>();
            String[] methodNames = this.methodAccess.getMethodNames();
            for (int i = 0, n = methodNames.length; i < n; i++) {
                String methodName = methodNames[i];
                if (methodName.startsWith(Symbol.GETTER_PREFIX.getSymbol())) {
                    getters.put(methodName, i);
                }
                if (methodName.startsWith(Symbol.SETTER_PREFIX.getSymbol())) {
                    setters.put(methodName, i);
                }
            }
        }

        public MethodAccess getMethodAccess() {
            return methodAccess;
        }

        public Map<String, Integer> getGetters() {
            return getters;
        }

        public Map<String, Integer> getSetters() {
            return setters;
        }
    }

}
