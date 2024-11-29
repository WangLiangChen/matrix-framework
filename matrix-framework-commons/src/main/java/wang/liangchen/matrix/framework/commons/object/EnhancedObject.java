package wang.liangchen.matrix.framework.commons.object;

import wang.liangchen.matrix.framework.commons.type.ClassUtil;

import java.io.Serializable;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Liangchen.Wang 2022-04-01 21:46
 * 增强版Object
 * 不能实现Map接口，否则该类及其子类的属性将会被隐藏
 */
public class EnhancedObject implements Serializable {
    /**
     * 对象扩展属性和属性值
     */
    private final Map<String, Object> extendedFields = new EnhancedMap<>();

    public static <T extends EnhancedObject> T newInstance(Class<T> clazz, boolean initializeFields) {
        T object = ClassUtil.INSTANCE.instantiate(clazz);
        if (initializeFields) {
            ClassUtil.INSTANCE.initializeFields(object);
        }
        return object;
    }

    public static <T extends EnhancedObject> T newInstance(Class<T> clazz) {
        return ClassUtil.INSTANCE.instantiate(clazz);
    }

    public void initializeFields() {
        ClassUtil.INSTANCE.initializeFields(this);
    }

    public <E> E copyPropertiesTo(Class<E> targetClass) {
        return JavaBeanUtil.INSTANCE.copyProperties(this, targetClass);
    }

    public <E> void copyPropertiesFrom(Object object) {
        JavaBeanUtil.INSTANCE.copyProperties(object, this);
    }

    public void addExtendedField(String name, Object value) {
        this.extendedFields.put(name, value);
    }

    public void addExtendedFields(Map<String, Object> extendedFields) {
        this.extendedFields.putAll(extendedFields);
    }

    public Map<String, Object> getExtendedFields() {
        return extendedFields;
    }

    public void removeExtendedField(String name) {
        this.extendedFields.remove(name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "EnhancedObject[", "]")
                .add("extendedFields=" + extendedFields)
                .toString();
    }


}
