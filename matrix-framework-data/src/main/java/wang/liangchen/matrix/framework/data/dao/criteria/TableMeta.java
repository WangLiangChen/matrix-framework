package wang.liangchen.matrix.framework.data.dao.criteria;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2021-10-22 9:35
 */
public class TableMeta {
    private final Class<?> entityClass;
    private final String tableName;
    private final Map<String, ColumnMeta> columnMetas;
    private final Map<String, ColumnMeta> pkColumnMetas = new HashMap<>();
    private final Map<String, ColumnMeta> nonPkColumnMetas = new HashMap<>();
    private final ColumnMeta columnDeleteMeta;
    private final ColumnMeta columnVersionMeta;

    private TableMeta(Class<?> entityClass, String tableName, Map<String, ColumnMeta> columnMetas) {
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.columnMetas = columnMetas;

        ColumnMeta columnDeleteMeta = null;
        ColumnMeta columnStateMeta = null;
        ColumnMeta columnVersionMeta = null;
        Collection<ColumnMeta> values = columnMetas.values();
        for (ColumnMeta columnMeta : values) {
            if (columnMeta.isId()) {
                this.pkColumnMetas.put(columnMeta.getFieldName(), columnMeta);
            }
            if (columnMeta.isNotId()) {
                this.nonPkColumnMetas.put(columnMeta.getFieldName(), columnMeta);
            }
            if (null == columnVersionMeta && columnMeta.isVersion()) {
                columnVersionMeta = columnMeta;
            }
            if (null == columnDeleteMeta && null != columnMeta.getMarkDeleteValue()) {
                columnDeleteMeta = columnMeta;
            }
        }
        this.columnVersionMeta = columnVersionMeta;
        this.columnDeleteMeta = columnDeleteMeta;
    }

    public static TableMeta newInstance(Class<?> entityClass, String tableName, Map<String, ColumnMeta> columnMetas) {
        return new TableMeta(entityClass, tableName, columnMetas);
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, ColumnMeta> getColumnMetas() {
        return columnMetas;
    }


    public Map<String, ColumnMeta> getPkColumnMetas() {
        return pkColumnMetas;
    }

    public Map<String, ColumnMeta> getNonPkColumnMetas() {
        return nonPkColumnMetas;
    }

    public ColumnMeta getColumnDeleteMeta() {
        return columnDeleteMeta;
    }

    public ColumnMeta getColumnVersionMeta() {
        return columnVersionMeta;
    }
}
