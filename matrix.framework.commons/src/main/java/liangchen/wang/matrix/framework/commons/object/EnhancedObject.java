package liangchen.wang.matrix.framework.commons.object;

import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;
import liangchen.wang.matrix.framework.commons.exception.MatrixInfoException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("NullableProblems")
public abstract class EnhancedObject implements Serializable, Cloneable {
    /**
     * 动态fields，用于动态扩展类的filed
     */
    private transient Map<String, Object> extendedFields = new ConcurrentHashMap<>();


    public int size() {
        return extendedFields.size();
    }


    public boolean isEmpty() {
        return extendedFields.isEmpty();
    }


    public boolean containsKey(Object key) {
        return extendedFields.containsKey(key);
    }


    public boolean containsValue(Object value) {
        return extendedFields.containsValue(value);
    }


    public Object get(Object key) {
        return extendedFields.get(key);
    }


    public Object put(String key, Object value) {
        if (null == value) {
            throw new MatrixInfoException("Can't put null,instead of liangchen.wang.matrix.framework.commons.object.NullValue");
        }
        return extendedFields.put(key, value);
    }


    public Object remove(Object key) {
        return extendedFields.remove(key);
    }


    public void putAll(Map<? extends String, ?> map) {
        extendedFields.putAll(map);
    }


    public void clear() {
        extendedFields.clear();
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


    public Object getOrDefault(Object key, Object defaultValue) {
        return extendedFields.getOrDefault(key, defaultValue);
    }


    public void forEach(BiConsumer<? super String, ? super Object> action) {
        extendedFields.forEach(action);
    }


    public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
        extendedFields.replaceAll(function);
    }


    public Object putIfAbsent(String key, Object value) {
        return extendedFields.putIfAbsent(key, value);
    }


    public boolean remove(Object key, Object value) {
        return extendedFields.remove(key, value);
    }


    public boolean replace(String key, Object oldValue, Object newValue) {
        return extendedFields.replace(key, oldValue, newValue);
    }


    public Object replace(String key, Object value) {
        return extendedFields.replace(key, value);
    }


    public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
        return extendedFields.computeIfAbsent(key, mappingFunction);
    }


    public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return extendedFields.computeIfPresent(key, remappingFunction);
    }


    public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return extendedFields.compute(key, remappingFunction);
    }


    public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return extendedFields.merge(key, value, remappingFunction);
    }

    public Map<String, Object> getExtendedFields() {
        return extendedFields;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new MatrixErrorException(e);
        }
    }
}
