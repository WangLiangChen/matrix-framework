package wang.liangchen.matrix.framework.data.datasource.dialect;

public class MySQLDialect extends AbstractDialect {
    public final static String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    public MySQLDialect() {
        super(DRIVER_CLASS_NAME);
    }
}
