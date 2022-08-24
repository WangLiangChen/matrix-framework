package wang.liangchen.matrix.framework.data.datasource.dialect;


import wang.liangchen.matrix.framework.commons.string.StringUtil;

/**
 * @author LiangChen.Wang
 */
public final class PostgreSQLDialect extends AbstractDialect {
    public final static String EXCEPTION_CLASS = "org.postgresql.util.PSQLException";

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

    public enum PSQLState {
        UNIQUE_VIOLATION("23505");
        private final String state;

        private PSQLState(String state) {
            this.state = state;
        }

        public String getState() {
            return this.state;
        }
    }
}
