package wang.liangchen.matrix.framework.data.dao.entity;

import java.io.Serializable;

/**
 * @author Liangchen.Wang 2023-03-23 21:51
 */
public class ExtendedColumnValue implements Serializable {
    private String columnName;
    private String columnValue;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }
}
