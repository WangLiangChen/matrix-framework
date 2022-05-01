package wang.liangchen.matrix.framework.data.dao.criteria;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiangChen.Wang
 */
public final class SqlBuilder {
    private final String sql;
    private final List<Object[]> args;

    public static SqlBuilder newInstance(String sql) {
        return new SqlBuilder(sql);
    }

    public SqlBuilder(String sql) {
        this.sql = sql;
        this.args = new ArrayList<>();
    }

    public void addArgs(Object... objects) {
        args.add(objects);
    }

    public void addArgs(List<Object> objects) {
        args.add(objects.toArray());
    }

    public String getSql() {
        return sql;
    }

    public List<Object[]> getArgs() {
        return args;
    }
}
