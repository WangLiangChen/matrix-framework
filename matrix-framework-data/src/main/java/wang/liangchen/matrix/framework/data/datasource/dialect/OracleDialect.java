package wang.liangchen.matrix.framework.data.datasource.dialect;


/**
 * @author LiangChen.Wang
 */
public final class OracleDialect extends AbstractDialect {

    public OracleDialect() {
        super("Oracle");
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
