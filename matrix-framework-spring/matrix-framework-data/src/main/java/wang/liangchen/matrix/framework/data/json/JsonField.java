package wang.liangchen.matrix.framework.data.json;


import wang.liangchen.matrix.framework.commons.object.EnhancedMap;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author Liangchen.Wang 2022-12-12 13:05
 */
public final class JsonField implements Serializable {
    private final Map<String, Object> delegate;

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

    public Map<String, Object> findDelegate() {
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
    public int hashCode() {
        return Objects.hashCode(delegate);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        JsonField jsonField = (JsonField) object;
        return Objects.equals(delegate, jsonField.delegate);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "JsonField[", "]")
                .add("delegate=" + delegate)
                .toString();
    }
}
