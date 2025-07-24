package wang.liangchen.matrix.framework.data.criteria;

import wang.liangchen.matrix.framework.commons.object.EnhancedList;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
import wang.liangchen.matrix.framework.data.pagination.Pagination;

import java.util.*;

/**
 * @author LiangChen.Wang
 */
class QueryParameter extends EnhancedObject {
    private String driverClassName;
    private String dataSourceName;
    private String tableName;

    private String whereSql;
    private Map<String, Object> whereSqlValues;

    private final List<String> selectColumns = new EnhancedList<>();
    private final Pagination pagination = new Pagination();
    /**
     * 是否拼接distinct
     */
    private Boolean distinct;
    /**
     * 是否拼接for update
     */
    private Boolean forUpdate;

    private Object softDeleteColumnValue;


    public void addSelectColumn(String selectColumn) {
        selectColumns.add(selectColumn);
    }

    public void addSelectColumns(Collection<String> selectColumns) {
        this.selectColumns.addAll(selectColumns);
    }

    public List<String> getSelectColumns() {
        return selectColumns;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    public Boolean getForUpdate() {
        return forUpdate;
    }

    public void setForUpdate(Boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    public String getWhereSql() {
        return whereSql;
    }

    public void setWhereSql(String whereSql) {
        this.whereSql = whereSql;
    }

    public Map<String, Object> getWhereSqlValues() {
        return whereSqlValues;
    }

    public void setWhereSqlValues(Map<String, Object> whereSqlValues) {
        this.whereSqlValues = whereSqlValues;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public Object getSoftDeleteColumnValue() {
        return softDeleteColumnValue;
    }

    public void setSoftDeleteColumnValue(Object softDeleteColumnValue) {
        this.softDeleteColumnValue = softDeleteColumnValue;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), driverClassName, dataSourceName, tableName, whereSql, whereSqlValues, selectColumns, pagination, distinct, forUpdate, softDeleteColumnValue);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        QueryParameter that = (QueryParameter) object;
        return Objects.equals(driverClassName, that.driverClassName) && Objects.equals(dataSourceName, that.dataSourceName) && Objects.equals(tableName, that.tableName) && Objects.equals(whereSql, that.whereSql) && Objects.equals(whereSqlValues, that.whereSqlValues) && Objects.equals(selectColumns, that.selectColumns) && Objects.equals(pagination, that.pagination) && Objects.equals(distinct, that.distinct) && Objects.equals(forUpdate, that.forUpdate) && Objects.equals(softDeleteColumnValue, that.softDeleteColumnValue);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "QueryParameter[", "]")
                .add("driverClassName='" + driverClassName + "'")
                .add("dataSourceName='" + dataSourceName + "'")
                .add("tableName='" + tableName + "'")
                .add("whereSql='" + whereSql + "'")
                .add("whereSqlValues=" + whereSqlValues)
                .add("selectColumns=" + selectColumns)
                .add("pagination=" + pagination)
                .add("distinct=" + distinct)
                .add("forUpdate=" + forUpdate)
                .add("softDeleteColumnValue=" + softDeleteColumnValue)
                .toString();
    }
}
