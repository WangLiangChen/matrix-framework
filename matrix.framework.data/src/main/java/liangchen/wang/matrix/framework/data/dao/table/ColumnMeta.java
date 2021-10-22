package liangchen.wang.matrix.framework.data.dao.table;

import liangchen.wang.matrix.framework.data.annotation.Query;

/**
 * @author Liangchen.Wang 2021-10-22 9:37
 */
public class ColumnMeta {
    private final String columnName;
    private final boolean isId;
    private final Query queryAnnotation;
    private final String fieldName;
    private final Class<?> fieldType;

    private ColumnMeta(String columnName, boolean isId, Query queryAnnotation, String fieldName, Class<?> fieldType) {
        this.columnName = columnName;
        this.isId = isId;
        this.queryAnnotation = queryAnnotation;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public static ColumnMeta newInstance(String columnName, boolean isId, Query queryAnnotation, String fieldName, Class<?> fieldType) {
        return new ColumnMeta(columnName, isId, queryAnnotation, fieldName, fieldType);
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

    public Query getQueryAnnotation() {
        return queryAnnotation;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }
}
