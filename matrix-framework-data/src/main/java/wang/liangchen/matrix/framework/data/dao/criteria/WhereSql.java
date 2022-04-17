package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.table.TableMeta;
import wang.liangchen.matrix.framework.data.pagination.PaginationParameter;

import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-16 22:40
 */
public class WhereSql extends PaginationParameter {
    private final TableMeta tableMeta;
    private final String sql;
    private final Map<String, Object> values;
    private final String resultColumns;

    private WhereSql(TableMeta tableMeta, String sql, Map<String, Object> values, String resultColumns) {
        this.tableMeta = tableMeta;
        this.sql = sql;
        this.values = values;
        this.resultColumns = resultColumns;
    }

    public static WhereSql newInstance(TableMeta tableMeta, String sql, Map<String, Object> values, String resultColumns) {
        return new WhereSql(tableMeta, sql, values, resultColumns);
    }

    public String getSql() {
        return sql;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public String getResultColumns() {
        return resultColumns;
    }

    public TableMeta getTableMeta() {
        return tableMeta;
    }
}
