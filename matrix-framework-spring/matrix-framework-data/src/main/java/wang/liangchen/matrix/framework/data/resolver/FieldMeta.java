package wang.liangchen.matrix.framework.data.resolver;

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
    private final Map<FieldLabel, Optional<String>> fieldLabels;


    public FieldMeta(String fieldName, Class<?> fieldClass, Type fieldType, String columnName, Map<FieldLabel, Optional<String>> fieldLabels) {
        this.fieldName = fieldName;
        this.fieldClass = fieldClass;
        this.fieldType = fieldType;
        this.columnName = columnName;
        this.fieldLabels = fieldLabels;
    }

    public boolean checkFieldLabel(FieldLabel fieldLabel) {
        return fieldLabels.containsKey(fieldLabel);
    }

    public Optional<String> fieldLabelValue(FieldLabel fieldLabel) {
        return fieldLabels.get(fieldLabel);
    }

    public IdStrategy.Strategy getIdStrategy() {
        return fieldLabels.get(FieldLabel.ID).map(IdStrategy.Strategy::valueOf).orElse(IdStrategy.Strategy.NONE);
    }

    public String getSoftDeleteValue() {
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

    public Map<FieldLabel, Optional<String>> getFieldLabels() {
        return fieldLabels;
    }
}
