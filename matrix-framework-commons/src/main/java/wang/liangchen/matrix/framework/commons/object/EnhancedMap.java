package wang.liangchen.matrix.framework.commons.object;

import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Liangchen.Wang 2022-04-01 21:46
 */
public class EnhancedMap implements Map<String, Object>, Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private final Map<String, Object> map;

    public EnhancedMap() {
        this(DEFAULT_INITIAL_CAPACITY, false);
    }

    public EnhancedMap(Map<String, Object> map) {
        if (map == null) {
            throw new IllegalArgumentException("map is null.");
        }
        this.map = map;
    }

    public EnhancedMap(boolean ordered) {
        this(DEFAULT_INITIAL_CAPACITY, ordered);
    }

    public EnhancedMap(int initialCapacity) {
        this(initialCapacity, false);
    }

    public EnhancedMap(int initialCapacity, boolean ordered) {
        if (ordered) {
            map = new LinkedHashMap<>(initialCapacity);
        } else {
            map = new HashMap<>(initialCapacity);
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        boolean contain = map.containsKey(key);
        if (contain) {
            return true;
        }
        if (key instanceof Number || key instanceof Character || key instanceof Boolean || key instanceof UUID) {
            contain = map.containsKey(String.valueOf(key));
        }
        return contain;
    }

    @Override
    public boolean containsValue(Object object) {
        return map.containsValue(object);
    }

    @Override
    public Object get(Object key) {
        Object object = map.get(key);
        if (object == null) {
            if (key instanceof Number || key instanceof Character || key instanceof Boolean || key instanceof UUID) {
                object = map.get(String.valueOf(key));
            }
        }
        return object;
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        Object object;
        return ((object = get(key)) != null) ? object : defaultValue;
    }
    @SuppressWarnings("unchecked")
    public EnhancedMap getEnhancedMap(String key) {
        Object object = map.get(key);
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
        Object object = map.get(key);
        if (object instanceof EnhancedList) {
            return (EnhancedList) object;
        }

        if (object instanceof List) {
            return new EnhancedList((List) object);
        }
        throw new MatrixInfoException("object must be List or EnhancedList");
    }

    public <T> T getObject(String key) {
        Object object = map.get(key);
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

    @Override
    public Object put(String key, Object object) {
        return map.put(key, object);
    }

    public EnhancedMap fluentPut(String key, Object object) {
        map.put(key, object);
        return this;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        map.putAll(m);
    }

    public EnhancedMap fluentPutAll(Map<? extends String, ?> m) {
        map.putAll(m);
        return this;
    }

    @Override
    public void clear() {
        map.clear();
    }

    public EnhancedMap fluentClear() {
        map.clear();
        return this;
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    public EnhancedMap fluentRemove(Object key) {
        map.remove(key);
        return this;
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public EnhancedMap clone() {
        return new EnhancedMap(map instanceof LinkedHashMap ? new LinkedHashMap<>(map) : new HashMap<>(map));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object instanceof EnhancedMap) {
            return this.map.equals(((EnhancedMap) object).map);
        }

        return this.map.equals(object);
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    public Map<String, Object> getNativeMap() {
        return this.map;
    }

}
