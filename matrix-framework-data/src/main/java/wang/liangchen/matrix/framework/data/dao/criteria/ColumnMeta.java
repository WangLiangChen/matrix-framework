package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.type.ClassUtil;

/**
 * @author Liangchen.Wang 2021-10-22 9:37
 */
public class ColumnMeta {
    private final String fieldName;
    private final Class<?> fieldType;
    private final String fieldClassName;
    private final String columnName;
    private final boolean id;
    private final boolean unique;
    private final boolean version;
    private final String markDeleteValue;

    private ColumnMeta(String fieldName, Class<?> fieldType, String columnName, boolean id, boolean unique, boolean version, String markDeleteValue) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldClassName = fieldType.getName().replace("java.lang", "");
        ;
        this.columnName = columnName;

        this.id = id;
        this.unique = unique;
        this.version = version;

        this.markDeleteValue = markDeleteValue;
    }

    private ColumnMeta(String fieldName, String fieldClassName, String columnName, boolean id, boolean unique, boolean version, String markDeleteValue) {
        this.fieldName = fieldName;
        this.fieldType = ClassUtil.INSTANCE.forName(fieldClassName);
        this.fieldClassName = fieldClassName.replace("java.lang.", "");
        this.columnName = columnName;

        this.id = id;
        this.unique = unique;
        this.version = version;

        this.markDeleteValue = markDeleteValue;
    }

    public static ColumnMeta newInstance(String fieldName, Class<?> fieldType, String columnName, boolean isId, boolean isUnique, boolean isVersion, String markDeleteValue) {
        return new ColumnMeta(fieldName, fieldType, columnName, isId, isUnique, isVersion, markDeleteValue);
    }

    public static ColumnMeta newInstance(String fieldName, String fieldClassName, String columnName, boolean isId, boolean isUnique, boolean isVersion, String markDeleteValue) {
        return new ColumnMeta(fieldName, fieldClassName, columnName, isId, isUnique, isVersion, markDeleteValue);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public String getFieldClassName() {
        return fieldClassName;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isId() {
        return id;
    }

    public boolean isNotId() {
        return !id;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isNotUnique() {
        return !unique;
    }

    public boolean isVersion() {
        return version;
    }

    public boolean isNotVersion() {
        return !version;
    }

    public String getMarkDeleteValue() {
        return markDeleteValue;
    }


}
