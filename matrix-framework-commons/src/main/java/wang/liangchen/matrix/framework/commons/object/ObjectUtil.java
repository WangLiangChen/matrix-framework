package wang.liangchen.matrix.framework.commons.object;

import net.sf.cglib.beans.BeanCopier;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.number.NumberUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liangchen.Wang 2021-09-30 15:22
 */
public enum ObjectUtil {
    /**
     * instance
     */
    INSTANCE;
    private final static Map<String, BeanCopier> BEANCOPIER_CACHE = new ConcurrentHashMap<>();

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

    public boolean isEmpty(Object object) {
        if (null == object) {
            return true;
        }
        if (object instanceof String) {
            return object.toString().isEmpty();
        }
        if (object instanceof Map) {
            return ((Map<?, ?>) object).isEmpty();
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).isEmpty();
        }
        Class<?> type = object.getClass();
        if (type.isArray()) {
            return Array.getLength(object) == 0;
        }
        return true;
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

    public String castToString(Object object) {
        if (object == null) {
            return null;
        }
        return String.valueOf(object);
    }

    public Byte castToByte(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Number) {
            return ((Number) object).byteValue();
        }
        if (object instanceof BigDecimal) {
            return NumberUtil.INSTANCE.decimalToByte((BigDecimal) object);
        }
        if (object instanceof Boolean) {
            return (Boolean) object ? (byte) 1 : (byte) 0;
        }
        if (object instanceof String) {
            String string = String.valueOf(object);
            if (StringUtil.INSTANCE.isInteger(string)) {
                return Byte.parseByte(string);
            }
        }
        throw new MatrixInfoException("can not cast to byte, object :{} ", object);
    }

    public Character castToChar(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Character) {
            return (Character) object;
        }
        if (object instanceof String) {
            String string = (String) object;
            if (string.length() == 0) {
                return null;
            }
            if (string.length() != 1) {
                throw new MatrixInfoException("can not cast to char, object : {}", object);
            }
            return string.charAt(0);
        }
        throw new MatrixInfoException("can not cast to char, object :{} ", object);
    }

    public Short castToShort(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof BigDecimal) {
            return NumberUtil.INSTANCE.decimalToShort((BigDecimal) object);
        }

        if (object instanceof Number) {
            return ((Number) object).shortValue();
        }
        if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue() ? (short) 1 : (short) 0;
        }
        if (object instanceof String) {
            String string = String.valueOf(object);
            if (StringUtil.INSTANCE.isInteger(string)) {
                return Short.parseShort(string);
            }
        }
        throw new MatrixInfoException("can not cast to short, object : {}", object);
    }

    public Integer castToInt(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof BigDecimal) {
            return NumberUtil.INSTANCE.decimalToInt((BigDecimal) object);
        }

        if (object instanceof Number) {
            return ((Number) object).intValue();
        }
        if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue() ? 1 : 0;
        }
        if (object instanceof String) {
            String string = String.valueOf(object);
            if (StringUtil.INSTANCE.isInteger(string)) {
                return Integer.parseInt(string);
            }
        }
        throw new MatrixInfoException("can not cast to int, object : {}", object);
    }

    public Long castToLong(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof BigDecimal) {
            return NumberUtil.INSTANCE.decimalToLong((BigDecimal) object);
        }

        if (object instanceof Number) {
            return ((Number) object).longValue();
        }
        if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue() ? 1L : 0L;
        }
        if (object instanceof String) {
            String string = String.valueOf(object);
            if (StringUtil.INSTANCE.isInteger(string)) {
                return Long.parseLong(string);
            }
        }
        throw new MatrixInfoException("can not cast to long, object : {}", object);
    }

    public BigDecimal castToBigDecimal(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Float) {
            Float floatobject = (Float) object;
            if (Float.isNaN(floatobject) || Float.isInfinite(floatobject)) {
                return null;
            }
        }
        if (object instanceof Double) {
            Double doubleobject = (Double) object;
            if (Double.isNaN(doubleobject) || Double.isInfinite(doubleobject)) {
                return null;
            }
        }
        if (object instanceof Map && ((Map) object).size() == 0) {
            return null;
        }
        if (object instanceof BigDecimal) {
            return (BigDecimal) object;
        }
        if (object instanceof BigInteger) {
            return new BigDecimal((BigInteger) object);
        }
        // 统一转换成字符串
        String string = String.valueOf(object);
        int length = string.length();
        if (length == 0 || string.equalsIgnoreCase("null")) {
            return null;
        }
        return new BigDecimal(string);
    }

    public BigInteger castToBigInteger(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Float) {
            Float floatobject = (Float) object;
            if (Float.isNaN(floatobject) || Float.isInfinite(floatobject)) {
                return null;
            }
            return BigInteger.valueOf(floatobject.longValue());
        }
        if (object instanceof Double) {
            Double doubleobject = (Double) object;
            if (Double.isNaN(doubleobject) || Double.isInfinite(doubleobject)) {
                return null;
            }
            return BigInteger.valueOf(doubleobject.longValue());
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

        String string = object.toString();
        int length = string.length();

        if (length == 0 || string.equalsIgnoreCase("null")) {
            return null;
        }
        return new BigInteger(string);
    }

    public Float castToFloat(Object object) {
        if (object == null) {
            return null;
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

        throw new MatrixInfoException("can not cast to float, object :{}", object);
    }

    public Double castToDouble(Object object) {
        if (object == null) {
            return null;
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

        throw new MatrixInfoException("can not cast to double, object :{}", object);
    }

    public Boolean castToBoolean(Object object) {
        if (object == null) {
            return null;
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
        throw new MatrixInfoException("can not cast to boolean, object : {}", object);
    }

    public byte[] castToBytes(Object object) {
        if (object instanceof byte[]) {
            return (byte[]) object;
        }
        throw new MatrixInfoException("can not cast to byte[], object : " + object);
    }


    public <T> T copyProperties(Object source, Class<T> targetClass) {
        Class<?> sourceClass = source.getClass();
        String copierCacheKey = String.format("%s-->%s", sourceClass.getName(), targetClass.getName());
        BeanCopier beanCopier = BEANCOPIER_CACHE.computeIfAbsent(copierCacheKey,
                key -> BeanCopier.create(sourceClass, targetClass, false));
        T target = ClassUtil.INSTANCE.instantiate(targetClass);
        beanCopier.copy(source, target, null);
        return target;
    }

    public void copyProperties(Object source, Object target) {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();
        String copierCacheKey = String.format("%s-->%s", sourceClass.getName(), targetClass.getName());
        BeanCopier beanCopier = BEANCOPIER_CACHE.computeIfAbsent(copierCacheKey,
                key -> BeanCopier.create(sourceClass, targetClass, false));
        beanCopier.copy(source, target, null);
    }
}
