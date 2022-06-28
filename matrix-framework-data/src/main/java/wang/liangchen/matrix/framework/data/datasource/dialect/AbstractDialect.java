package wang.liangchen.matrix.framework.data.datasource.dialect;


/**
 * @author LiangChen.Wang
 */
public abstract class AbstractDialect implements IDialect {
    private final String dataSourceType;

    protected AbstractDialect(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public abstract String resolveCountSql(String targetSql);

    public abstract String resolvePaginationSql(String targetSql);

    public String getDataSourceType() {
        return dataSourceType;
    }
}
