package wang.liangchen.matrix.framework.data.dao.table;

/**
 * @author Liangchen.Wang 2021-10-22 9:37
 */
public class ColumnMeta {
    private final String fieldName;
    private final Class<?> fieldType;
    private final String columnName;
    private final boolean isId;
    private final boolean isUnique;
    private final boolean isVersion;
    private final String deleteValue;
    private final boolean isState;

    private ColumnMeta(String fieldName, Class<?> fieldType, String columnName, boolean isId, boolean isUnique, boolean isVersion, boolean isState, String deleteValue) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.columnName = columnName;

        this.isId = isId;
        this.isUnique = isUnique;
        this.isVersion = isVersion;
        this.isState = isState;

        this.deleteValue = deleteValue;
    }

    public static ColumnMeta newInstance(String fieldName, Class<?> fieldType, String columnName, boolean isId, boolean isUnique, boolean isVersion, boolean isState, String deleteValue) {
        return new ColumnMeta(fieldName, fieldType, columnName, isId, isUnique, isVersion, isState, deleteValue);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getFieldType() {
        return fieldType;
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

    public boolean isState() {
        return isState;
    }

    public boolean isNotState() {
        return !isState;
    }

    public String getDeleteValue() {
        return deleteValue;
    }
}
