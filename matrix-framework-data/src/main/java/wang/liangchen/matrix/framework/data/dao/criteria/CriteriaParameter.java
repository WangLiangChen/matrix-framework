package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.table.TableMeta;
import wang.liangchen.matrix.framework.data.pagination.PaginationParameter;

import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-17 23:14
 */
public class CriteriaParameter extends PaginationParameter {
    private TableMeta tableMeta;
    private String tableName;
    private String whereSql;
    private Map<String, Object> whereSqlValues;

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

}
