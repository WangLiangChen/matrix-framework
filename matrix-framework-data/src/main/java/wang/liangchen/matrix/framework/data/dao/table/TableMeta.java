package wang.liangchen.matrix.framework.data.dao.table;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2021-10-22 9:35
 */
public class TableMeta {
    private final String tableName;
    private final Map<String, ColumnMeta> columnMetas;
    private final Map<String, ColumnMeta> pkColumnMetas;
    private final Map<String, ColumnMeta> nonPkColumnMetas;

    private TableMeta(String tableName, Map<String, ColumnMeta> columnMetas) {
        this.tableName = tableName;
        this.columnMetas = columnMetas;
        this.pkColumnMetas = columnMetas.entrySet().stream().filter(e -> e.getValue().isId()).collect(Collectors.toMap(
                e -> e.getKey(),
                e -> e.getValue()
        ));

        this.nonPkColumnMetas = columnMetas.entrySet().stream().filter(e -> e.getValue().isNotId()).collect(Collectors.toMap(
                e -> e.getKey(),
                e -> e.getValue()
        ));
    }

    public static TableMeta newInstance(String tableName,Map<String,ColumnMeta> columnMetas) {
        return new TableMeta(tableName, columnMetas);
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String,ColumnMeta> getColumnMetas() {
        return columnMetas;
    }


    public Map<String,ColumnMeta> getPkColumnMetas() {
        return pkColumnMetas;
    }

    public Map<String,ColumnMeta> getNonPkColumnMetas() {
        return nonPkColumnMetas;
    }
}
