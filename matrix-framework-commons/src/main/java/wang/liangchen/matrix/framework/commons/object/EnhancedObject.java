package wang.liangchen.matrix.framework.commons.object;

import wang.liangchen.matrix.framework.commons.type.ClassUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-01 21:46
 * 不能实现Map接口，否则该类子类的属性将会被隐藏
 */
public class EnhancedObject implements Serializable {

    /**
     * 对象扩展属性 需要被序列化
     */
    private final Map<String, Object> extendedFields = new HashMap<>();

    public <E> E to(Class<E> targetClass) {
        return ObjectUtil.INSTANCE.copyProperties(this, targetClass);
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

    public static <T extends EnhancedObject> T newInstance(Class<T> clazz) {
        return ClassUtil.INSTANCE.instantiate(clazz);
    }

    public static <T extends EnhancedObject> T newInstance(Class<T> clazz, boolean initializeFields) {
        T object = ClassUtil.INSTANCE.instantiate(clazz);
        if (initializeFields) {
            ClassUtil.INSTANCE.initializeFields(object);
        }
        return object;
    }

    public static <T extends EnhancedObject> T valueOf(Object source, Class<T> clazz) {
        return ObjectUtil.INSTANCE.copyProperties(source, clazz);
    }

    public void initializeFields() {
        ClassUtil.INSTANCE.initializeFields(this);
    }
}
