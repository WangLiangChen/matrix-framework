package wang.liangchen.matrix.framework.data.json;


import wang.liangchen.matrix.framework.commons.object.EnhancedMap;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-12-12 13:05
 */
public final class JsonField implements Serializable {
    private Map<String, Object> delegate;

    public JsonField() {
        this(new EnhancedMap<>());
    }

    public JsonField(Map<String, Object> map) {
        this.delegate = map;
    }

    public static JsonField newInstance(Map<String, Object> map) {
        return new JsonField(map);
    }

    public static JsonField newInstance() {
        return new JsonField();
    }

    public Map<String, Object> getDelegate() {
        return delegate;
    }

    public Object put(String key, Object value) {
        return this.delegate.put(key, value);
    }

    public void putAll(Map<String, Object> map) {
        this.delegate.putAll(map);
    }

    public Object putIfAbsent(String key, Object value) {
        return this.delegate.putIfAbsent(key, value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.delegate);
    }
}
