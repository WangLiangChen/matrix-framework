package wang.liangchen.matrix.framework.data.criteria;

import java.util.StringJoiner;

/**
 * @author Liangchen.Wang 2023-04-29 10:20
 */
public class VersionColumnMeta {
    private final String columnName;
    private final Object expectedValue;
    private final Object value;

    private VersionColumnMeta(String columnName, Object expectedValue, Object value) {
        this.columnName = columnName;
        this.expectedValue = expectedValue;
        this.value = value;
    }

    public static VersionColumnMeta newInstance(String columnName, Object expectedValue, Object value) {
        return new VersionColumnMeta(columnName, expectedValue, value);
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getExpectedValue() {
        return expectedValue;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "VersionColumnMeta[", "]")
                .add("columnName='" + columnName + "'")
                .add("expectedValue=" + expectedValue)
                .add("value=" + value)
                .toString();
    }
}
