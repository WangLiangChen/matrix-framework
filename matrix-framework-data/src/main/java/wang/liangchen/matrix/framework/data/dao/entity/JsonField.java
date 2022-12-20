package wang.liangchen.matrix.framework.data.dao.entity;

import wang.liangchen.matrix.framework.commons.object.EnhancedMap;

import java.util.Map;

/**
 * @author Liangchen.Wang 2022-12-12 13:05
 */
public final class JsonField extends EnhancedMap {
    public JsonField() {
    }

    private JsonField(Map<String, Object> extendedFields) {
        super(extendedFields);
    }


    public static JsonField newInstance() {
        return new JsonField();
    }

    public static JsonField newInstance(Map<String, Object> extendedFields) {
        return new JsonField(extendedFields);
    }

    @Override
    public JsonField fluentPut(String key, Object object) {
        this.put(key, object);
        return this;
    }

    @Override
    public EnhancedMap fluentPutAll(Map<? extends String, ?> map) {
        this.putAll(map);
        return this;
    }
}
