package wang.liangchen.matrix.framework.commons.object;

import com.esotericsoftware.reflectasm.MethodAccess;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.number.NumberUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author Liangchen.Wang 2021-09-30 15:22
 */
public enum ObjectUtil {
    /**
     * instance
     */
    INSTANCE;
    private static final Schema<ProtostuffWrapper> PROTOSTUFF_WRAPPER_SCHEMA = RuntimeSchema.getSchema(ProtostuffWrapper.class);

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

    @SuppressWarnings("unchecked")
    public <T> T cast(Object object) {
        if (null == object) {
            return null;
        }
        return (T) object;
    }

    public String castToString(Object object, String defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        return String.valueOf(object);
    }

    public String castToString(Object object) {
        return castToString(object, null);
    }

    public Byte castToByte(Object object, Byte defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof BigDecimal) {
            return NumberUtil.INSTANCE.decimalToByte((BigDecimal) object);
        }

        if (object instanceof Number) {
            return ((Number) object).byteValue();
        }
        if (object instanceof String) {
            String string = String.valueOf(object);
            if (StringUtil.INSTANCE.isInteger(string)) {
                return Byte.parseByte(string);
            }
        }
        if (object instanceof Boolean) {
            return (Boolean) object ? (byte) 1 : (byte) 0;
        }
        throw new MatrixWarnException("can not cast to byte, object :{} ", object);
    }

    public Byte castToByte(Object object) {
        return castToByte(object, null);
    }

    public Character castToChar(Object object, Character defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Character) {
            return (Character) object;
        }
        if (object instanceof String) {
            String string = (String) object;
            if (string.length() != 1) {
                throw new MatrixWarnException("can not cast to char, object : {}", object);
            }
            return string.charAt(0);
        }
        throw new MatrixWarnException("can not cast to char, object :{} ", object);
    }

    public Character castToChar(Object object) {
        return castToChar(object, null);
    }

    public Short castToShort(Object object, Short defaultValue) {
        if (object == null) {
            return defaultValue;
        }

        if (object instanceof BigDecimal) {
            return NumberUtil.INSTANCE.decimalToShort((BigDecimal) object);
        }

        if (object instanceof Number) {
            return ((Number) object).shortValue();
        }
        if (object instanceof Boolean) {
            return (Boolean) object ? (short) 1 : (short) 0;
        }
        if (object instanceof String) {
            String string = String.valueOf(object);
            if (StringUtil.INSTANCE.isInteger(string)) {
                return Short.parseShort(string);
            }
        }
        throw new MatrixWarnException("can not cast to short, object : {}", object);
    }

    public Short castToShort(Object object) {
        return castToShort(object, null);
    }

    public Integer castToInt(Object object, Integer defaultValue) {
        if (object == null) {
            return defaultValue;
        }

        if (object instanceof BigDecimal) {
            return NumberUtil.INSTANCE.decimalToInt((BigDecimal) object);
        }

        if (object instanceof Number) {
            return ((Number) object).intValue();
        }
        if (object instanceof Boolean) {
            return (Boolean) object ? 1 : 0;
        }
        if (object instanceof String) {
            String string = String.valueOf(object);
            if (StringUtil.INSTANCE.isInteger(string)) {
                return Integer.parseInt(string);
            }
        }
        throw new MatrixWarnException("can not cast to int, object : {}", object);
    }

    public Integer castToInt(Object object) {
        return castToInt(object, null);
    }

    public Long castToLong(Object object, Long defaultValue) {
        if (object == null) {
            return defaultValue;
        }

        if (object instanceof BigDecimal) {
            return NumberUtil.INSTANCE.decimalToLong((BigDecimal) object);
        }

        if (object instanceof Number) {
            return ((Number) object).longValue();
        }
        if (object instanceof Boolean) {
            return (Boolean) object ? 1L : 0L;
        }
        if (object instanceof String) {
            String string = String.valueOf(object);
            if (StringUtil.INSTANCE.isInteger(string)) {
                return Long.parseLong(string);
            }
        }
        throw new MatrixWarnException("can not cast to long, object : {}", object);
    }

    public Long castToLong(Object object) {
        return castToLong(object, null);
    }

    public BigDecimal castToBigDecimal(Object object, BigDecimal defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof BigDecimal) {
            return (BigDecimal) object;
        }
        if (object instanceof BigInteger) {
            return new BigDecimal((BigInteger) object);
        }
        if (object instanceof Float) {
            float floatobject = (float) object;
            if (Float.isNaN(floatobject) || Float.isInfinite(floatobject)) {
                throw new MatrixWarnException("can not cast to BigDecimal, object : {}", object);
            }
        }
        if (object instanceof Double) {
            double doubleobject = (double) object;
            if (Double.isNaN(doubleobject) || Double.isInfinite(doubleobject)) {
                throw new MatrixWarnException("can not cast to BigDecimal, object : {}", object);
            }
        }
        if (object instanceof Map && ((Map<?, ?>) object).size() == 0) {
            throw new MatrixWarnException("can not cast to BigDecimal, object : {}", object);
        }

        // 统一转换成字符串
        String string = String.valueOf(object);
        int length = string.length();
        if (length == 0 || string.equalsIgnoreCase("null")) {
            throw new MatrixWarnException("can not cast to BigDecimal, object : {}", object);
        }
        return new BigDecimal(string);
    }

    public BigDecimal castToBigDecimal(Object object) {
        return castToBigDecimal(object, null);
    }

    public BigInteger castToBigInteger(Object object, BigInteger defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof BigInteger) {
            return (BigInteger) object;
        }
        if (object instanceof BigDecimal) {
            BigDecimal decimal = (BigDecimal) object;
            int scale = decimal.scale();
            if (scale > -1000 && scale < 1000) {
                return ((BigDecimal) object).toBigInteger();
            }
        }
        if (object instanceof Float) {
            Float floatobject = (Float) object;
            if (Float.isNaN(floatobject) || Float.isInfinite(floatobject)) {
                throw new MatrixWarnException("can not cast to BigInteger, object : {}", object);
            }
            return BigInteger.valueOf(floatobject.longValue());
        }
        if (object instanceof Double) {
            Double doubleobject = (Double) object;
            if (Double.isNaN(doubleobject) || Double.isInfinite(doubleobject)) {
                throw new MatrixWarnException("can not cast to BigInteger, object : {}", object);
            }
            return BigInteger.valueOf(doubleobject.longValue());
        }
        String string = object.toString();
        int length = string.length();

        if (length == 0 || string.equalsIgnoreCase("null")) {
            throw new MatrixWarnException("can not cast to BigInteger, object : {}", object);
        }
        return new BigInteger(string);
    }

    public BigInteger castToBigInteger(Object object) {
        return castToBigInteger(object, null);
    }

    public Float castToFloat(Object object, Float defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Number) {
            return ((Number) object).floatValue();
        }
        if (object instanceof String) {
            String string = String.valueOf(object);
            if (StringUtil.INSTANCE.isFloat(string)) {
                return Float.parseFloat(string);
            }
        }
        if (object instanceof Boolean) {
            return (Boolean) object ? 1F : 0F;
        }

        throw new MatrixWarnException("can not cast to float, object :{}", object);
    }

    public Float castToFloat(Object object) {
        return castToFloat(object, null);
    }

    public Double castToDouble(Object object, Double defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }
        if (object instanceof String) {
            String string = String.valueOf(object);
            if (StringUtil.INSTANCE.isFloat(string)) {
                return Double.parseDouble(string);
            }
        }

        if (object instanceof Boolean) {
            return (Boolean) object ? 1D : 0D;
        }

        throw new MatrixWarnException("can not cast to double, object :{}", object);
    }

    public Double castToDouble(Object object) {
        return castToDouble(object, null);
    }

    public Boolean castToBoolean(Object object, Boolean defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Boolean) {
            return (Boolean) object;
        }

        if (object instanceof BigDecimal) {
            return NumberUtil.INSTANCE.decimalToLong((BigDecimal) object) == 1;
        }

        if (object instanceof Number) {
            return ((Number) object).longValue() == 1;
        }

        if (object instanceof String) {
            String string = (String) object;
            if (string.length() == 0 || "null".equals(string) || "NULL".equals(string)) {
                return null;
            }
            if ("true".equalsIgnoreCase(string) || "1".equals(string)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(string) || "0".equals(string)) {
                return Boolean.FALSE;
            }
            if ("Y".equalsIgnoreCase(string) || "T".equals(string)) {
                return Boolean.TRUE;
            }
            if ("F".equalsIgnoreCase(string) || "N".equals(string)) {
                return Boolean.FALSE;
            }
        }
        throw new MatrixWarnException("can not cast to boolean, object : {}", object);
    }

    public Boolean castToBoolean(Object object) {
        return castToBoolean(object, null);
    }

    public byte[] castToBytes(Object object) {
        if (object instanceof byte[]) {
            return (byte[]) object;
        }
        throw new MatrixWarnException("can not cast to byte[], object : " + object);
    }

    public <S, T> List<T> copyProperties(Collection<S> sources, Class<T> targetClass) {
        return copyProperties(sources, targetClass, null);
    }

    public <S, T> List<T> copyProperties(Collection<S> sources, Class<T> targetClass, BiConsumer<S, T> consumer) {
        if (CollectionUtil.INSTANCE.isEmpty(sources)) {
            return new ArrayList<>(0);
        }
        List<T> targets = new ArrayList<>(sources.size());
        for (S source : sources) {
            T target = copyProperties(source, targetClass);
            if (null != consumer) {
                consumer.accept(source, target);
            }
            targets.add(target);
        }
        return targets;
    }

    public <T> T copyProperties(Object source, Class<T> targetClass) {
        T target = ClassUtil.INSTANCE.instantiate(targetClass);
        copyProperties(source, target);
        return target;
    }

    public void copyProperties(Object source, Object target) {
        ValidationUtil.INSTANCE.notNull(source, "source must not be null");
        ValidationUtil.INSTANCE.notNull(target, "target must not be null");
        ClassUtil.MethodAccessor sourceMethodAccessor = ClassUtil.INSTANCE.methodAccessor(source.getClass());
        ClassUtil.MethodAccessor targetMethodAccessor = ClassUtil.INSTANCE.methodAccessor(target.getClass());
        MethodAccess sourceMethodAccess = sourceMethodAccessor.getMethodAccess();
        MethodAccess targetMethodAccess = targetMethodAccessor.getMethodAccess();
        Map<String, Integer> getters = sourceMethodAccessor.getGetters();
        Map<String, Integer> setters = targetMethodAccessor.getSetters();
        setters.forEach((setter, setterIndex) -> {
            String fieldName = setter.substring(3);
            String getter = StringUtil.INSTANCE.getGetter(fieldName);
            Integer getterIndex = getters.get(getter);
            if (null == getterIndex) {
                return;
            }
            Object returnValue = sourceMethodAccess.invoke(source, getterIndex);
            targetMethodAccess.invoke(target, setterIndex, returnValue);
        });
    }

    public <T> byte[] protostuffSerializer(T object) {
        ProtostuffWrapper<T> wrapper = new ProtostuffWrapper<>();
        wrapper.setObject(object);
        LinkedBuffer allocate = LinkedBuffer.allocate();
        try {
            return ProtostuffIOUtil.toByteArray(wrapper, PROTOSTUFF_WRAPPER_SCHEMA, allocate);
        } finally {
            allocate.clear();
        }
    }

    public <T> T protostuffDeserializer(byte[] bytes) {
        ProtostuffWrapper<T> wrapper = new ProtostuffWrapper<>();
        ProtostuffIOUtil.mergeFrom(bytes, wrapper, PROTOSTUFF_WRAPPER_SCHEMA);
        return wrapper.getObject();
    }


    static class ProtostuffWrapper<T> {
        private T object;

        public T getObject() {
            return object;
        }

        public void setObject(T object) {
            this.object = object;
        }
    }

    static class CopierId {
        private final Class<?> sourceClass;
        private final Class<?> targetClass;

        CopierId(Class<?> sourceClass, Class<?> targetClass) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
        }

        public Class<?> getSourceClass() {
            return sourceClass;
        }

        public Class<?> getTargetClass() {
            return targetClass;
        }
    }
}
