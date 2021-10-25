package liangchen.wang.matrix.framework.data.dao.table;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2021-10-22 9:35
 */
public class TableMeta {
    private final String tableName;
    private final Set<ColumnMeta> columnMetas;
    private final Set<ColumnMeta> pkColumnMetas;
    private final Set<ColumnMeta> NonPkColumnMetas;

    private TableMeta(String tableName, Set<ColumnMeta> columnMetas) {
        this.tableName = tableName;
        this.columnMetas = columnMetas;
        this.pkColumnMetas = columnMetas.stream().filter(ColumnMeta::isId).collect(Collectors.toSet());
        this.NonPkColumnMetas = columnMetas.stream().filter(ColumnMeta::isNotId).collect(Collectors.toSet());
    }

    public static TableMeta newInstance(String tableName, Set<ColumnMeta> columnMetas) {
        return new TableMeta(tableName, columnMetas);
    }

    public String getTableName() {
        return tableName;
    }

    public Set<ColumnMeta> getColumnMetas() {
        return columnMetas;
    }


    public Set<ColumnMeta> getPkColumnMetas() {
        return pkColumnMetas;
    }

    public Set<ColumnMeta> getNonPkColumnMetas() {
        return NonPkColumnMetas;
    }
}
