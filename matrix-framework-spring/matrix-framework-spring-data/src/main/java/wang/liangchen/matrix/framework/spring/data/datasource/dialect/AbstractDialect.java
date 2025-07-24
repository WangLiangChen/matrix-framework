package wang.liangchen.matrix.framework.spring.data.datasource.dialect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LiangChen.Wang
 */
public abstract class AbstractDialect implements IDialect {
    private final String driverClassName;
    private static final Map<String, AbstractDialect> dialects = new ConcurrentHashMap<>();

    protected AbstractDialect(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public static AbstractDialect resolveDialect(String driverClassName) {
        return dialects.computeIfAbsent(driverClassName, key -> {
            switch (key) {
                case MySQLDialect.DRIVER_CLASS_NAME:
                    return new MySQLDialect();
                case PostgreSQLDialect.DRIVER_CLASS_NAME:
                    return new PostgreSQLDialect();
                default:
                    return null;
            }
        });
    }
}