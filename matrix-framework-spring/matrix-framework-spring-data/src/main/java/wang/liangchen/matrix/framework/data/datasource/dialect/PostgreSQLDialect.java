package wang.liangchen.matrix.framework.data.datasource.dialect;

/**
 * @author LiangChen.Wang
 */
public class PostgreSQLDialect extends AbstractDialect {
    public final static String DRIVER_CLASS_NAME = "org.postgresql.Driver";

    public PostgreSQLDialect() {
        super(DRIVER_CLASS_NAME);
    }
}
