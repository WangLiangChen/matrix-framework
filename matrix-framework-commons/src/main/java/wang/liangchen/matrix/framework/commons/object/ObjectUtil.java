package wang.liangchen.matrix.framework.commons.object;

import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

/**
 * @author Liangchen.Wang 2021-09-30 15:22
 */
public enum ObjectUtil {
    /**
     * instance
     */
    INSTANCE;
    private final static Map<Class<?>, Map<Class<?>, Function<Object, Object>>> castFunctions = new HashMap<Class<?>, Map<Class<?>, Function<Object, Object>>>() {{
        // from String to x
        Map<Class<?>, Function<Object, Object>> fromString = new HashMap<Class<?>, Function<Object, Object>>() {{
            put(Long.class, source -> Long.valueOf(String.valueOf(source)));
            put(Integer.class, source -> Integer.valueOf(String.valueOf(source)));
            put(Short.class, source -> Short.valueOf(String.valueOf(source)));
            put(Byte.class, source -> Byte.valueOf(String.valueOf(source)));
            put(Double.class, source -> Double.valueOf(String.valueOf(source)));
            put(Float.class, source -> Float.valueOf(String.valueOf(source)));
            put(Character.class, source -> String.valueOf(source).charAt(0));
            put(Boolean.class, source -> Boolean.valueOf(String.valueOf(source)));
            put(BigDecimal.class, source -> new BigDecimal(String.valueOf(source)));
            put(BigInteger.class, source -> new BigInteger(String.valueOf(source)));
        }};
        put(String.class, fromString);
        // from Number to x
        Map<Class<?>, Function<Object, Object>> fromNumber = new HashMap<Class<?>, Function<Object, Object>>() {{
            put(String.class, String::valueOf);
            put(Long.class, source -> ((Number) source).longValue());
            put(Integer.class, source -> ((Number) source).intValue());
            put(Short.class, source -> ((Number) source).shortValue());
            put(Byte.class, source -> ((Number) source).byteValue());
            put(Double.class, source -> ((Number) source).doubleValue());
            put(Float.class, source -> ((Number) source).floatValue());
            put(Character.class, source -> (char) ((Number) source).intValue());
            put(Boolean.class, source -> ((Number) source).intValue() == 1);
            put(BigDecimal.class, source -> BigDecimal.valueOf(((Number) source).doubleValue()));
            put(BigInteger.class, source -> BigInteger.valueOf(((Number) source).longValue()));
        }};
        put(Number.class, fromNumber);
        // from Boolean to x
        Map<Class<?>, Function<Object, Object>> fromBoolean = new HashMap<Class<?>, Function<Object, Object>>() {{
            put(String.class, String::valueOf);
            put(Long.class, source -> (Boolean) source ? 1L : 0L);
            put(Integer.class, source -> (Boolean) source ? 1 : 0);
            put(Short.class, source -> (Boolean) source ? (short) 1 : (short) 0);
            put(Byte.class, source -> (Boolean) source ? (byte) 1 : (byte) 0);
            put(Double.class, source -> (Boolean) source ? 1D : 0D);
            put(Float.class, source -> (Boolean) source ? 1F : 0F);
            put(Character.class, source -> (Boolean) source ? 'Y' : 'N');
            put(BigDecimal.class, source -> (Boolean) source ? BigDecimal.ONE : BigDecimal.ZERO);
            put(BigInteger.class, source -> (Boolean) source ? BigInteger.ONE : BigInteger.ZERO);
        }};
        put(Boolean.class, fromBoolean);
        // from Character to x
        Map<Class<?>, Function<Object, Object>> fromCharacter = new HashMap<Class<?>, Function<Object, Object>>() {{
            put(String.class, String::valueOf);
            put(Long.class, source -> (long) (char) source);
            put(Integer.class, source -> (int) (char) source);
            put(Short.class, source -> (short) (char) source);
            put(Byte.class, source -> (byte) (char) source);
            put(Double.class, source -> (double) (char) source);
            put(Float.class, source -> (float) (char) source);
            put(Boolean.class, source -> (char) source == 'Y' || (char) source == 'T');
            put(BigDecimal.class, source -> BigDecimal.valueOf((char) source));
            put(BigInteger.class, source -> BigInteger.valueOf((char) source));
        }};
        put(Character.class, fromCharacter);
        // from BigDecimal to x
        Map<Class<?>, Function<Object, Object>> fromBigDecimal = new HashMap<Class<?>, Function<Object, Object>>() {{
            put(String.class, source -> ((BigDecimal) source).toPlainString());
            put(Long.class, source -> ((BigDecimal) source).longValue());
            put(Integer.class, source -> ((BigDecimal) source).intValue());
            put(Short.class, source -> ((BigDecimal) source).shortValue());
            put(Byte.class, source -> ((BigDecimal) source).byteValue());
            put(Double.class, source -> ((BigDecimal) source).doubleValue());
            put(Float.class, source -> ((BigDecimal) source).floatValue());
            put(Character.class, source -> (char) ((BigDecimal) source).intValue());
            put(Boolean.class, source -> ((BigDecimal) source).longValue() == 1);
            put(BigInteger.class, source -> ((BigDecimal) source).toBigInteger());
        }};
        put(BigDecimal.class, fromBigDecimal);
        // from BigInteger to x
        Map<Class<?>, Function<Object, Object>> fromBigInteger = new HashMap<Class<?>, Function<Object, Object>>() {{
            put(String.class, String::valueOf);
            put(Long.class, source -> ((BigInteger) source).longValue());
            put(Integer.class, source -> ((BigInteger) source).intValue());
            put(Short.class, source -> ((BigInteger) source).shortValue());
            put(Byte.class, source -> ((BigInteger) source).byteValue());
            put(Double.class, source -> ((BigInteger) source).doubleValue());
            put(Float.class, source -> ((BigInteger) source).floatValue());
            put(Character.class, source -> (char) ((BigInteger) source).intValue());
            put(Boolean.class, source -> ((BigInteger) source).longValue() == 1);
            put(BigDecimal.class, source -> new BigDecimal((BigInteger) source));
        }};
        put(BigInteger.class, fromBigInteger);
    }};

    @SuppressWarnings("unchecked")
    public <T> T cast(Object object, T defaultValue) {
        if (null == object) {
            return defaultValue;
        }
        return (T) object;
    }

    public <T> T cast(Object object) {
        return cast(object, null);
    }

    public <T, R> R castTo(T source, Class<?> targetClass) {
        if (null == source) {
            return null;
        }
        Class<?> sourceClass = source.getClass();
        if (Number.class.isAssignableFrom(sourceClass)) {
            sourceClass = Number.class;
        }
        Map<Class<?>, Function<Object, Object>> classFunctionMap = castFunctions.get(sourceClass);
        if (null == classFunctionMap) {
            throw new MatrixWarnException("Unsupported source type: {}, the supported sources: {}" + sourceClass, castFunctions.keySet());
        }
        if (targetClass.isPrimitive()) {
            targetClass = ClassUtil.INSTANCE.primitiveToWrapper(targetClass);
        }
        Function<Object, Object> castFunction = classFunctionMap.get(targetClass);
        if (null == castFunction) {
            throw new MatrixWarnException("Unsupported target type: {}, the supported targets: {}" + targetClass, classFunctionMap.keySet());
        }
        Object target = castFunction.apply(source);
        return cast(target);
    }

    public <T, R> R innerValue(T t, Function<T, R> function) {
        return Optional.ofNullable(t).map(function).orElse(null);
    }

    public boolean isNull(Object object) {
        return null == object;
    }

    public boolean isNotNull(Object object) {
        return null != object;
    }

    public boolean isArray(Object object) {
        if (null == object) {
            return false;
        }
        return object.getClass().isArray();
    }

    public boolean isString(Object object) {
        if (null == object) {
            return false;
        }
        return object instanceof String;
    }

    public boolean isNumber(Object object) {
        if (null == object) {
            return false;
        }
        return object instanceof Number;
    }

    /**
     * String,Collection,Map,Array,Iterator,Iterrable
     *
     * @param object checked target
     * @return if empty
     */
    public boolean isEmpty(Object object) {
        if (null == object) {
            return true;
        }
        if (object instanceof String) {
            return object.toString().isEmpty();
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Map) {
            return ((Map<?, ?>) object).isEmpty();
        }
        if (isArray(object)) {
            return Array.getLength(object) == 0;
        }
        if (object instanceof Iterator) {
            return !((Iterator<?>) object).hasNext();
        }
        if (object instanceof Iterable) {
            return !((Iterable<?>) object).iterator().hasNext();
        }
        throw new MatrixWarnException("Unsupported type: " + object.getClass());
    }

    public boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public byte[] castToBytes(Object object) {
        if (object instanceof byte[]) {
            return (byte[]) object;
        }
        throw new MatrixWarnException("can not cast to byte[], object : " + object);
    }
}
