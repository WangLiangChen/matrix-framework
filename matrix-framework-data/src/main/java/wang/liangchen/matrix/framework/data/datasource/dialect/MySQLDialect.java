package wang.liangchen.matrix.framework.data.datasource.dialect;


/**
 * @author LiangChen.Wang
 */
public final class MySQLDialect extends AbstractDialect {
    private static final String HIBERNATE_DIALECT_CLASS = "org.hibernate.dialect.MySQLDialect";

    @Override
    public String resolveCountSql(String targetSql) {
        StringBuilder sb = new StringBuilder("select count(0) from ");
        targetSql = targetSql.toLowerCase();

        if (targetSql.lastIndexOf("order") > targetSql.lastIndexOf(")")) {
            sb.append(targetSql, targetSql.indexOf("from") + 4, targetSql.lastIndexOf("order"));
        } else {
            sb.append(targetSql.substring(targetSql.indexOf("from") + 4));
        }
        return sb.toString();
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
