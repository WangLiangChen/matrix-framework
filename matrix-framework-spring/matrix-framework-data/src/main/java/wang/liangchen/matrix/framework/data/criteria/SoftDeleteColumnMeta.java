package wang.liangchen.matrix.framework.data.criteria;

import java.util.StringJoiner;

/**
 * @author Liangchen.Wang 2023-04-29 10:20
 */
public class SoftDeleteColumnMeta {
    private final String columnName;
    private final Object value;

    public SoftDeleteColumnMeta(String columnName, Object value) {
        this.columnName = columnName;
        this.value = value;
    }

    public static SoftDeleteColumnMeta newInstance(String columnName, Object value) {
        return new SoftDeleteColumnMeta(columnName, value);
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "SoftDeleteColumnMeta[", "]")
                .add("columnName='" + columnName + "'")
                .add("value='" + value + "'")
                .toString();
    }
}
