package wang.liangchen.matrix.framework.data.datasource.dialect;


import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author LiangChen.Wang
 */
public final class MySQLDialect extends AbstractDialect {

    public final static String CONSTRAINT_EXCEPTION_CLASS = SQLIntegrityConstraintViolationException.class.getName();

    public MySQLDialect() {
        super("MySQL");
    }

    @Override
    public String resolveCountSql(String targetSql) {
        return targetSql;
    }

    @Override
    public String resolvePaginationSql(String targetSql) {
        return targetSql;
    }


}
