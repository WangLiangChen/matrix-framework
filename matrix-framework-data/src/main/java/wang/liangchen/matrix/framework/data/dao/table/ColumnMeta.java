package wang.liangchen.matrix.framework.data.dao.table;

import wang.liangchen.matrix.framework.data.annotation.Query;

/**
 * @author Liangchen.Wang 2021-10-22 9:37
 */
public class ColumnMeta {
    private final String columnName;
    private final boolean isId;
    private final boolean isUnique;
    private final boolean isVersion;
    private final String fieldName;
    private final Class<?> fieldType;

    private ColumnMeta(String columnName, boolean isId, boolean isUnique, boolean isVersion, String fieldName, Class<?> fieldType) {
        this.columnName = columnName;
        this.isId = isId;
        this.isUnique = isUnique;
        this.isVersion = isVersion;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public static ColumnMeta newInstance(String columnName, boolean isId, boolean isUnique, boolean isVersion, String fieldName, Class<?> fieldType) {
        return new ColumnMeta(columnName, isId, isUnique, isVersion, fieldName, fieldType);
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isId() {
        return isId;
    }

    public boolean isNotId() {
        return !isId;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public boolean isNotUnique() {
        return !isUnique;
    }

    public boolean isVersion() {
        return isVersion;
    }

    public boolean isNotVersion() {
        return !isVersion;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }
}
