package wang.liangchen.matrix.framework.data.dao.entity;

import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.enumeration.DataType;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Liangchen.Wang 2023-03-23 21:51
 */
@Entity(name = "matrix_columns")
public class ExtendedColumn extends RootEntity {
    @Id
    @IdStrategy(IdStrategy.Strategy.MatrixFlake)
    private Long columnId;
    private String columnKey;
    private String columnGroup;
    private String tableName;
    private String columnName;
    private DataType dataType = DataType.STRING;
    private String columnDefault = StringUtil.INSTANCE.blankString();
    private Byte isNullable = 1;
    private String columnRegex = StringUtil.INSTANCE.blankString();
    private String columnComment = StringUtil.INSTANCE.blankString();

    public static ExtendedColumn newInstance() {
        return ClassUtil.INSTANCE.instantiate(ExtendedColumn.class);
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public String getColumnGroup() {
        return columnGroup;
    }

    public void setColumnGroup(String columnGroup) {
        this.columnGroup = columnGroup;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getColumnDefault() {
        return columnDefault;
    }

    public void setColumnDefault(String columnDefault) {
        this.columnDefault = columnDefault;
    }

    public Byte getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(Byte isNullable) {
        this.isNullable = isNullable;
    }

    public String getColumnRegex() {
        return columnRegex;
    }

    public void setColumnRegex(String columnRegex) {
        this.columnRegex = columnRegex;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

}
