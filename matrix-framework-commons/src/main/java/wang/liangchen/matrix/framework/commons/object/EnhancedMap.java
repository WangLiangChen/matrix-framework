package wang.liangchen.matrix.framework.commons.object;

import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Liangchen.Wang 2022-04-01 21:46
 * 不能实现Map接口，否则该类子类的属性将会被隐藏
 */
public class EnhancedMap implements Serializable {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    /**
     * 对象扩展属性 需要被序列化
     */
    private final Map<String, Object> delegate;

    public EnhancedMap(int initialCapacity, boolean ordered) {
        if (ordered) {
            delegate = new LinkedHashMap<>(initialCapacity);
        } else {
            delegate = new HashMap<>(initialCapacity);
        }
    }

    public EnhancedMap(Map<String, Object> delegate) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, delegate, "extendedFields must not be null");
        this.delegate = delegate;
    }

    public EnhancedMap() {
        this(DEFAULT_INITIAL_CAPACITY, false);
    }

    public EnhancedMap(boolean ordered) {
        this(DEFAULT_INITIAL_CAPACITY, ordered);
    }

    public EnhancedMap(int initialCapacity) {
        this(initialCapacity, false);
    }

    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public boolean containsKey(Object key) {
        return delegate.containsKey(String.valueOf(key));
    }

    public boolean containsValue(Object object) {
        return delegate.containsValue(object);
    }

    public Object get(Object key) {
        return delegate.get(String.valueOf(key));
    }

    public Object getOrDefault(Object key, Object defaultValue) {
        Object object;
        return ((object = get(key)) != null) ? object : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public EnhancedMap getEnhancedMap(String key) {
        Object object = delegate.get(key);
        if (object instanceof EnhancedMap) {
            return (EnhancedMap) object;
        }
        if (object instanceof Map) {
            return new EnhancedMap((Map<String, Object>) object);
        }
        throw new MatrixWarnException("object must be Map or EnhancedMap");
    }

    @SuppressWarnings("unchecked")
    public EnhancedList getEnhancedList(String key) {
        Object object = delegate.get(key);
        if (object instanceof EnhancedList) {
            return (EnhancedList) object;
        }

        if (object instanceof List) {
            return new EnhancedList((List<Object>) object);
        }
        throw new MatrixWarnException("object must be List or EnhancedList");
    }

    public <T> T getObject(String key) {
        Object object = delegate.get(key);
        return ObjectUtil.INSTANCE.cast(object);
    }

    public byte[] getBytes(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToBytes(object);
    }

    public Boolean getBoolean(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToBoolean(object);
    }

    public boolean getBooleanValue(String key) {
        Boolean object = getBoolean(key);
        if (null == object) {
            return false;
        }
        return object;
    }


    public Byte getByte(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToByte(object);
    }

    public byte getByteValue(String key) {
        Byte object = getByte(key);
        if (null == object) {
            return 0;
        }
        return object;
    }

    public Short getShort(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToShort(object);
    }

    public short getShortValue(String key) {
        Short object = getShort(key);
        if (object == null) {
            return 0;
        }
        return object;
    }

    public Integer getInteger(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToInt(object);
    }

    public int getIntegerValue(String key) {
        Integer object = getInteger(key);
        if (null == object) {
            return 0;
        }
        return object;
    }

    public Long getLong(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToLong(object);
    }

    public long getLongValue(String key) {
        Long object = getLong(key);
        if (object == null) {
            return 0L;
        }
        return object;
    }

    public Float getFloat(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToFloat(object);
    }

    public float getFloatValue(String key) {
        Float object = getFloat(key);
        if (object == null) {
            return 0f;
        }
        return object;
    }

    public Double getDouble(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToDouble(object);
    }

    public double getDoubleValue(String key) {
        Double object = getDouble(key);
        if (object == null) {
            return 0d;
        }
        return object;
    }

    public BigDecimal getBigDecimal(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToBigDecimal(object);
    }

    public BigInteger getBigInteger(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToBigInteger(object);
    }

    public String getString(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return String.valueOf(object);
    }


    public Object put(String key, Object object) {
        return delegate.put(key, object);
    }

    public EnhancedMap fluentPut(String key, Object object) {
        delegate.put(key, object);
        return this;
    }


    public void putAll(Map<? extends String, ?> m) {
        delegate.putAll(m);
    }

    public EnhancedMap fluentPutAll(Map<? extends String, ?> m) {
        delegate.putAll(m);
        return this;
    }


    public void clear() {
        delegate.clear();
    }

    public EnhancedMap fluentClear() {
        delegate.clear();
        return this;
    }


    public Object remove(Object key) {
        return delegate.remove(key);
    }

    public EnhancedMap fluentRemove(Object key) {
        delegate.remove(key);
        return this;
    }


    public Set<String> keySet() {
        return delegate.keySet();
    }


    public Collection<Object> values() {
        return delegate.values();
    }


    public Set<Map.Entry<String, Object>> entrySet() {
        return delegate.entrySet();
    }

    public Map<String, Object> getNativeMap() {
        return this.delegate;
    }

    public void addExtendedField(String key, Object value) {
        this.delegate.put(key, value);
    }

    public void addExtendedFields(Map<String, Object> extendedFields) {
        this.delegate.putAll(extendedFields);
    }

    public void removeExtendedField(String key) {
        this.delegate.remove(key);
    }

    public Map<String, Object> getExtendedFields() {
        return delegate;
    }
}
