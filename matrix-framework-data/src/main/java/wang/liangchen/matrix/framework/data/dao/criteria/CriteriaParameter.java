package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.pagination.QueryParameter;

import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-17 23:14
 */
public class CriteriaParameter<E extends RootEntity> extends QueryParameter {
    private String dataSourceType;
    private TableMeta tableMeta;
    private String tableName;
    private String whereSql;
    private Map<String, Object> whereSqlValues;

    private E entity;
    private Class<E> entityClass;

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public TableMeta getTableMeta() {
        return tableMeta;
    }

    public void setTableMeta(TableMeta tableMeta) {
        this.tableMeta = tableMeta;
        this.tableName = this.tableMeta.getTableName();
    }

    public String getWhereSql() {
        return whereSql;
    }

    public void setWhereSql(String whereSql) {
        this.whereSql = whereSql;
    }

    public void setWhereSqlValues(Map<String, Object> whereSqlValues) {
        this.whereSqlValues = whereSqlValues;
    }

    public Map<String, Object> getWhereSqlValues() {
        return whereSqlValues;
    }

    public String getTableName() {
        return tableName;
    }

    public E getEntity() {
        return entity;
    }

    public void setEntity(E entity) {
        this.entity = entity;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<E> entityClass) {
        this.entityClass = entityClass;
    }
}
