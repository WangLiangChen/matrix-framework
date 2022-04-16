package wang.liangchen.matrix.framework.data.dao.criteria;

import java.util.List;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-16 22:40
 */
public class WhereSql {
    private final String sql;
    private Map<String, Object> values;

    private WhereSql(String sql, Map<String, Object> values) {
        this.sql = sql;
        this.values = values;
    }

    public static WhereSql newInstance(String sql, Map<String, Object> values) {
        return new WhereSql(sql, values);
    }

    public String getSql() {
        return sql;
    }

    public Map<String, Object> getValues() {
        return values;
    }
}
