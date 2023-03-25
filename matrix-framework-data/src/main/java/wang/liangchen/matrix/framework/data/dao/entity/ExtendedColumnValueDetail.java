package wang.liangchen.matrix.framework.data.dao.entity;

import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.data.enumeration.DataType;

/**
 * @author Liangchen.Wang 2023-03-23 21:51
 */
public class ExtendedColumnValueDetail extends ExtendedColumnValue {
    private DataType dataType = DataType.STRING;
    private String columnComment = StringUtil.INSTANCE.blankString();

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }
}
