package liangchen.wang.matrix.framework.data.dao.table;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2021-10-22 9:35
 */
public class TableMeta {
    private final String tableName;
    private final Set<ColumnMeta> columnMetas;
    private final Set<String> ids;
    private final Set<String> columns;

    private TableMeta(String tableName, Set<ColumnMeta> columnMetas) {
        this.tableName = tableName;
        this.columnMetas = columnMetas;
        this.ids = columnMetas.stream().filter(ColumnMeta::isId).map(ColumnMeta::getColumnName).collect(Collectors.toSet());
        this.columns = columnMetas.stream().filter(ColumnMeta::isId).map(ColumnMeta::getColumnName).collect(Collectors.toSet());
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

    public Set<String> getIds() {
        return ids;
    }

    public Set<String> getColumns() {
        return columns;
    }
}
