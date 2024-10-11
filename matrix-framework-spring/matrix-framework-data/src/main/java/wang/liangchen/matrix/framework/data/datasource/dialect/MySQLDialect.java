package wang.liangchen.matrix.framework.data.datasource.dialect;

public class MySQLDialect extends AbstractDialect {
    public MySQLDialect() {
        super("com.mysql.cj.jdbc.Driver");
    }
}
