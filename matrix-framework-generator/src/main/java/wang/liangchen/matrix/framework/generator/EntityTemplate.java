package wang.liangchen.matrix.framework.generator;

import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;

import java.util.List;

/**
 * @author Liangchen.Wang 2022-04-26 14:46
 */
public class EntityTemplate {
    private String basePackage;
    private String subPackage;
    private String entityName;
    private List<ColumnMeta> columnMetas;
    private String tableName;

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getSubPackage() {
        return subPackage;
    }

    public void setSubPackage(String subPackage) {
        this.subPackage = subPackage;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<ColumnMeta> getColumnMetas() {
        return columnMetas;
    }

    public void setColumnMetas(List<ColumnMeta> columnMetas) {
        this.columnMetas = columnMetas;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
