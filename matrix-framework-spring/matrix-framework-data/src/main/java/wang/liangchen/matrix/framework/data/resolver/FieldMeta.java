package wang.liangchen.matrix.framework.data.resolver;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

/**
 * @author LiangChen.Wang 2024/10/15 18:01
 */
public final class FieldMeta {
    private final String fieldName;
    private final Class<?> fieldClass;
    private final Type fieldType;
    private final String columnName;
    private final Map<FieldLabel, Optional<Object>> fieldLabels;


    public FieldMeta(String fieldName, Class<?> fieldClass, Type fieldType, String columnName, Map<FieldLabel, Optional<Object>> fieldLabels) {
        this.fieldName = fieldName;
        this.fieldClass = fieldClass;
        this.fieldType = fieldType;
        this.columnName = columnName;
        this.fieldLabels = fieldLabels;
        if (fieldLabels.containsKey(FieldLabel.VERSION) && !(Long.class.isAssignableFrom(fieldClass) || Integer.class.isAssignableFrom(fieldClass))) {
            throw new MatrixErrorException("Version field must be of type Integer or Long");
        }
    }

    public boolean checkFieldLabel(FieldLabel fieldLabel) {
        return fieldLabels.containsKey(fieldLabel);
    }

    public Optional<Object> fieldLabelValue(FieldLabel fieldLabel) {
        return fieldLabels.get(fieldLabel);
    }

    public IdStrategy.Strategy getIdStrategy() {
        return fieldLabels.get(FieldLabel.ID).map(String::valueOf).map(IdStrategy.Strategy::valueOf).orElse(IdStrategy.Strategy.NONE);
    }

    public Object getSoftDeleteValue() {
        return fieldLabels.get(FieldLabel.SOFT_DELETE).orElse(null);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getFieldClass() {
        return fieldClass;
    }

    public Type getFieldType() {
        return fieldType;
    }

    public String getColumnName() {
        return columnName;
    }

}
