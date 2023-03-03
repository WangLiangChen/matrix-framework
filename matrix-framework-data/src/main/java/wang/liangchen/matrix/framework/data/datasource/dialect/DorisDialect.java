package wang.liangchen.matrix.framework.data.datasource.dialect;


/**
 * @author LiangChen.Wang
 */
public final class DorisDialect extends AbstractDialect {

    public DorisDialect() {
        super("Doris");
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
