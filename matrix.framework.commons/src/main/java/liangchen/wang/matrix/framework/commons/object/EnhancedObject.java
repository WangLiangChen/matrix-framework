package liangchen.wang.matrix.framework.commons.object;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("NullableProblems")
public abstract class EnhancedObject implements Serializable, Cloneable, Map<String, Object> {
    /**
     * 动态fields，用于动态扩展类的filed
     */
    private transient Map<String, Object> extendedFields = new ConcurrentHashMap<>();

    @Override
    public int size() {
        return extendedFields.size();
    }

    @Override
    public boolean isEmpty() {
        return extendedFields.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return extendedFields.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return extendedFields.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return extendedFields.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return extendedFields.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return extendedFields.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        extendedFields.putAll(map);
    }

    @Override
    public void clear() {
        extendedFields.clear();
    }

    @Override
    public Set<String> keySet() {
        return extendedFields.keySet();
    }

    @Override
    public Collection<Object> values() {
        return extendedFields.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return extendedFields.entrySet();
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        return extendedFields.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Object> action) {
        extendedFields.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
        extendedFields.replaceAll(function);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        return extendedFields.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return extendedFields.remove(key, value);
    }

    @Override
    public boolean replace(String key, Object oldValue, Object newValue) {
        return extendedFields.replace(key, oldValue, newValue);
    }

    @Override
    public Object replace(String key, Object value) {
        return extendedFields.replace(key, value);
    }

    @Override
    public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
        return extendedFields.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return extendedFields.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return extendedFields.compute(key, remappingFunction);
    }

    @Override
    public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return extendedFields.merge(key, value, remappingFunction);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
