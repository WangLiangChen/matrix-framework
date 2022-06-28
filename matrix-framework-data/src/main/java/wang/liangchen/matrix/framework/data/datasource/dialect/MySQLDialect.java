package wang.liangchen.matrix.framework.data.datasource.dialect;


/**
 * @author LiangChen.Wang
 */
public final class MySQLDialect extends AbstractDialect {


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
