package wang.liangchen.matrix.framework.commons.object;

import wang.liangchen.matrix.framework.commons.exception.MatrixExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Liangchen.Wang 2022-04-01 21:46
 * 实现了Map接口,该类及其子类的属性将会被隐藏
 */
public class EnhancedMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    /**
     * 对象扩展属性 需要被序列化
     */
    private final Map<K, V> delegate;

    public EnhancedMap(int initialCapacity, boolean ordered) {
        if (ordered) {
            delegate = new LinkedHashMap<>(initialCapacity);
        } else {
            delegate = new HashMap<>(initialCapacity);
        }
    }

    public EnhancedMap(Map<K, V> delegate) {
        ValidationUtil.INSTANCE.notNull(MatrixExceptionLevel.WARN, delegate, "parameter must not be null");
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

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public V get(Object key) {
        return delegate.get(key);
    }

    @Override
    public V put(K key, V value) {
        return delegate.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return delegate.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        delegate.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        delegate.replaceAll(function);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegate.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return delegate.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return delegate.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return delegate.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return delegate.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return delegate.merge(key, value, remappingFunction);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    public EnhancedMap<K, V> fluentPut(K key, V object) {
        this.put(key, object);
        return this;
    }


    public EnhancedMap<K, V> fluentPutAll(Map<? extends K, ? extends V> map) {
        this.putAll(map);
        return this;
    }


    public EnhancedMap<K, V> fluentClear() {
        this.clear();
        return this;
    }


    public EnhancedMap<K, V> fluentRemove(Object key) {
        this.remove(key);
        return this;
    }

    public Map<K, V> getNativeMap() {
        return this.delegate;
    }

    @SuppressWarnings("unchecked")
    public EnhancedMap<K, V> getEnhancedMap(Object key) {
        V object = this.get(key);
        if (null == object) {
            return null;
        }
        if (object instanceof EnhancedMap) {
            return (EnhancedMap<K, V>) object;
        }
        if (object instanceof Map) {
            return new EnhancedMap<>((Map<K, V>) object);
        }
        throw new MatrixWarnException("object must be Map or EnhancedMap");
    }

    @SuppressWarnings("unchecked")
    public <E> EnhancedList<E> getEnhancedList(String key) {
        Object object = this.get(key);
        if (null == object) {
            return null;
        }

        if (object instanceof EnhancedList) {
            return (EnhancedList<E>) object;
        }

        if (object instanceof List) {
            return new EnhancedList<>((List<E>) object);
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


    public void addExtendedField(K key, V value) {
        this.delegate.put(key, value);
    }

    public void addExtendedFields(Map<K, V> extendedFields) {
        this.delegate.putAll(extendedFields);
    }

    public void removeExtendedField(K key) {
        this.delegate.remove(key);
    }

    public Map<K, V> getExtendedFields() {
        return delegate;
    }

}
