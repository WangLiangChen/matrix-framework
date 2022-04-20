package wang.liangchen.matrix.framework.data.datasource.dialect;


import wang.liangchen.matrix.framework.commons.string.StringUtil;

/**
 * @author LiangChen.Wang
 */
public final class PostgreSQLDialect extends AbstractDialect {
    private static final String HIBERNATE_DIALECT_CLASS = "org.hibernate.dialect.PostgreSQL95Dialect";

    @Override
    public String resolveCountSql(String targetSql) {
       return StringUtil.INSTANCE.blankString();
    }

    @Override
    public String resolvePaginationSql(String targetSql) {
        return targetSql;
    }

    @Override
    public String setHibernateDialectClass() {
        return HIBERNATE_DIALECT_CLASS;
    }

}
