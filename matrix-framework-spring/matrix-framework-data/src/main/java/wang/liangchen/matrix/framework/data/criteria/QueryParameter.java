package wang.liangchen.matrix.framework.data.criteria;

import wang.liangchen.matrix.framework.commons.object.EnhancedList;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
import wang.liangchen.matrix.framework.data.pagination.Pagination;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author LiangChen.Wang
 */
class QueryParameter extends EnhancedObject {
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

    private String whereSql;
    private Map<String, Object> whereSqlValues;

    private String tableName;
    private String driverClassName;
    private String dataSourceName;


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

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }
}
