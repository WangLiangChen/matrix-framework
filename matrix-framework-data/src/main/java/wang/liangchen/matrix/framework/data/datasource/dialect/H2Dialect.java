package wang.liangchen.matrix.framework.data.datasource.dialect;

/**
 * @author Liangchen.Wang 2023-05-27 4:54
 */
public class H2Dialect extends AbstractDialect {
    public H2Dialect() {
        super("H2");
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
