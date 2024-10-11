package wang.liangchen.matrix.framework.data.datasource.dialect;

/**
 * @author LiangChen.Wang
 */
public abstract class AbstractDialect implements IDialect {
    private final String driverClassName;

    protected AbstractDialect(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }
}