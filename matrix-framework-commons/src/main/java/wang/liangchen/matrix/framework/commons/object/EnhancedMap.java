package wang.liangchen.matrix.framework.commons.object;

import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Liangchen.Wang 2022-04-01 21:46
 * 不能实现Map接口，否则该类子类的属性将会被隐藏
 */
public class EnhancedMap implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private final transient Map<String, Object> extendedFields;

    public EnhancedMap() {
        this(DEFAULT_INITIAL_CAPACITY, false);
    }

    public EnhancedMap(Map<String, Object> extendedFields) {
        if (extendedFields == null) {
            throw new IllegalArgumentException("map is null.");
        }
        this.extendedFields = extendedFields;
    }

    public EnhancedMap(boolean ordered) {
        this(DEFAULT_INITIAL_CAPACITY, ordered);
    }

    public EnhancedMap(int initialCapacity) {
        this(initialCapacity, false);
    }

    public EnhancedMap(int initialCapacity, boolean ordered) {
        if (ordered) {
            extendedFields = new LinkedHashMap<>(initialCapacity);
        } else {
            extendedFields = new HashMap<>(initialCapacity);
        }
    }


    public int size() {
        return extendedFields.size();
    }


    public boolean isEmpty() {
        return extendedFields.isEmpty();
    }


    public boolean containsKey(Object key) {
        boolean contain = extendedFields.containsKey(key);
        if (contain) {
            return true;
        }
        if (key instanceof Number || key instanceof Character || key instanceof Boolean || key instanceof UUID) {
            contain = extendedFields.containsKey(String.valueOf(key));
        }
        return contain;
    }


    public boolean containsValue(Object object) {
        return extendedFields.containsValue(object);
    }


    public Object get(Object key) {
        Object object = extendedFields.get(key);
        if (object == null) {
            if (key instanceof Number || key instanceof Character || key instanceof Boolean || key instanceof UUID) {
                object = extendedFields.get(String.valueOf(key));
            }
        }
        return object;
    }


    public Object getOrDefault(Object key, Object defaultValue) {
        Object object;
        return ((object = get(key)) != null) ? object : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public EnhancedMap getEnhancedMap(String key) {
        Object object = extendedFields.get(key);
        if (object instanceof EnhancedMap) {
            return (EnhancedMap) object;
        }

        if (object instanceof Map) {
            return new EnhancedMap((Map) object);
        }
        throw new MatrixInfoException("object must be Map or EnhancedMap");
    }

    @SuppressWarnings("unchecked")
    public EnhancedList getEnhancedList(String key) {
        Object object = extendedFields.get(key);
        if (object instanceof EnhancedList) {
            return (EnhancedList) object;
        }

        if (object instanceof List) {
            return new EnhancedList((List) object);
        }
        throw new MatrixInfoException("object must be List or EnhancedList");
    }

    public <T> T getObject(String key) {
        Object object = extendedFields.get(key);
        return ObjectUtil.INSTANCE.cast(object);
    }

    public Boolean getBoolean(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToBoolean(object);
    }

    public byte[] getBytes(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToBytes(object);
    }

    public boolean getBooleanValue(String key) {
        Object object = get(key);
        if (object == null) {
            return false;
        }
        return ObjectUtil.INSTANCE.castToBoolean(key).booleanValue();
    }

    public Byte getByte(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToByte(object);
    }

    public byte getByteValue(String key) {
        Object object = get(key);
        if (object == null) {
            return 0;
        }
        return ObjectUtil.INSTANCE.castToByte(object).byteValue();
    }

    public Short getShort(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToShort(object);
    }

    public short getShortValue(String key) {
        Object object = get(key);
        if (object == null) {
            return 0;
        }
        return ObjectUtil.INSTANCE.castToShort(object).shortValue();
    }

    public Integer getInteger(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToInt(object);
    }

    public int getIntValue(String key) {
        Object object = get(key);
        if (object == null) {
            return 0;
        }
        return ObjectUtil.INSTANCE.castToInt(object).intValue();
    }

    public Long getLong(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToLong(object);
    }

    public long getLongValue(String key) {
        Object object = get(key);
        if (object == null) {
            return 0L;
        }
        return ObjectUtil.INSTANCE.castToLong(object).longValue();
    }

    public Float getFloat(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToFloat(object);
    }

    public float getFloatValue(String key) {
        Object object = get(key);
        if (object == null) {
            return 0f;
        }
        return ObjectUtil.INSTANCE.castToFloat(object).floatValue();
    }

    public Double getDouble(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ObjectUtil.INSTANCE.castToDouble(object);
    }

    public double getDoubleValue(String key) {
        Object object = get(key);
        if (object == null) {
            return 0d;
        }
        return ObjectUtil.INSTANCE.castToDouble(object).doubleValue();
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
        return extendedFields.put(key, object);
    }

    public EnhancedMap fluentPut(String key, Object object) {
        extendedFields.put(key, object);
        return this;
    }


    public void putAll(Map<? extends String, ?> m) {
        extendedFields.putAll(m);
    }

    public EnhancedMap fluentPutAll(Map<? extends String, ?> m) {
        extendedFields.putAll(m);
        return this;
    }


    public void clear() {
        extendedFields.clear();
    }

    public EnhancedMap fluentClear() {
        extendedFields.clear();
        return this;
    }


    public Object remove(Object key) {
        return extendedFields.remove(key);
    }

    public EnhancedMap fluentRemove(Object key) {
        extendedFields.remove(key);
        return this;
    }


    public Set<String> keySet() {
        return extendedFields.keySet();
    }


    public Collection<Object> values() {
        return extendedFields.values();
    }


    public Set<Map.Entry<String, Object>> entrySet() {
        return extendedFields.entrySet();
    }


    public EnhancedMap clone() {
        return new EnhancedMap(extendedFields instanceof LinkedHashMap ? new LinkedHashMap<>(extendedFields) : new HashMap<>(extendedFields));
    }


    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object instanceof EnhancedMap) {
            return this.extendedFields.equals(((EnhancedMap) object).extendedFields);
        }

        return this.extendedFields.equals(object);
    }


    public int hashCode() {
        return this.extendedFields.hashCode();
    }

    public Map<String, Object> getNativeMap() {
        return this.extendedFields;
    }

    public void addExtendedField(String key, Object value) {
        this.extendedFields.put(key, value);
    }

    public void addExtendedFields(Map<String, Object> extendedFields) {
        this.extendedFields.putAll(extendedFields);
    }

    public void removeExtendedField(String key) {
        this.extendedFields.remove(key);
    }

    public Map<String, Object> getExtendedFields() {
        return extendedFields;
    }
}
