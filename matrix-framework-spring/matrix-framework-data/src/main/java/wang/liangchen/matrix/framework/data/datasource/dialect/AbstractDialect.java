package wang.liangchen.matrix.framework.data.datasource.dialect;

import java.util.Map;

/**
 * @author LiangChen.Wang
 */
public abstract class AbstractDialect implements IDialect {
    private final static Map<String, AbstractDialect> dialects = Map.of(
            MySQLDialect.DRIVER_CLASS_NAME, new MySQLDialect(),
            PostgreSQLDialect.DRIVER_CLASS_NAME, new PostgreSQLDialect()
    );
    private final String driverClassName;

    protected AbstractDialect(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public AbstractDialect getDialect() {
        return dialects.get(this.driverClassName);
    }

    public static AbstractDialect getDialect(String driverClassName) {
        return dialects.get(driverClassName);
    }
}