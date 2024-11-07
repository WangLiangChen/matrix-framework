package wang.liangchen.matrix.framework.data.entity;

/**
 * @author LiangChen.Wang 2024/10/18 14:06
 * 实体扩展的字段
 */
public class ExtendedField {
    private final String fieldName;
    private final Object fieldValue;
    private final Class<?> fieldClass;

    public ExtendedField(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.fieldClass = null == fieldValue ? null : fieldValue.getClass();

    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getFieldClass() {
        return fieldClass;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
