package wang.liangchen.matrix.framework.data.resolver;

import java.lang.reflect.Type;

/**
 * @author LiangChen.Wang 2024/10/15 18:01
 */
public final class FieldMeta {
    private final String fieldName;
    private final Class<?> fieldClass;
    private final Type fieldType;
    private final String columnName;
    private final boolean isColumnId;
    private final boolean isColumnUnique;
    private final boolean isColumnVersion;
    private final boolean isColumnJson;
    private final boolean isColumnState;
    private final String markDeleteValue;


    public FieldMeta(String fieldName, Class<?> fieldClass, Type fieldType, String columnName, boolean isColumnId, boolean isColumnUnique, boolean isColumnVersion, boolean isColumnJson, boolean isColumnState, String markDeleteValue) {
        this.fieldName = fieldName;
        this.fieldClass = fieldClass;
        this.fieldType = fieldType;
        this.columnName = columnName;
        this.isColumnId = isColumnId;
        this.isColumnUnique = isColumnUnique;
        this.isColumnVersion = isColumnVersion;
        this.isColumnJson = isColumnJson;
        this.isColumnState = isColumnState;
        this.markDeleteValue = markDeleteValue;
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

    public boolean isColumnId() {
        return isColumnId;
    }

    public boolean isColumnUnique() {
        return isColumnUnique;
    }

    public boolean isColumnVersion() {
        return isColumnVersion;
    }

    public boolean isColumnJson() {
        return isColumnJson;
    }

    public boolean isColumnState() {
        return isColumnState;
    }

    public String getMarkDeleteValue() {
        return markDeleteValue;
    }
}
