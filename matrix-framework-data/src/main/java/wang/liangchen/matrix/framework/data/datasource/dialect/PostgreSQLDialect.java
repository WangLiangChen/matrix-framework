package wang.liangchen.matrix.framework.data.datasource.dialect;


import wang.liangchen.matrix.framework.commons.string.StringUtil;

/**
 * @author LiangChen.Wang
 */
public final class PostgreSQLDialect extends AbstractDialect {

    public PostgreSQLDialect() {
        super("PostgreSQL");
    }

    @Override
    public String resolveCountSql(String targetSql) {
       return StringUtil.INSTANCE.blankString();
    }

    @Override
    public String resolvePaginationSql(String targetSql) {
        return targetSql;
    }


}
