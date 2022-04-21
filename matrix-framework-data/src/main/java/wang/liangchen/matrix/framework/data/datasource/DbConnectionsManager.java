package wang.liangchen.matrix.framework.data.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.springboot.context.BeanLoader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author LiangChen.Wang 2021/6/9
 */
public enum DbConnectionsManager {
    /**
     * instance
     */
    INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DataSource dataSource = BeanLoader.INSTANCE.getBean("dataSource");

    public Connection springManagedConnection() {
        try {
            return DataSourceUtils.doGetConnection(this.dataSource);
        } catch (SQLException ex) {
            throw new MatrixErrorException(ex);
        }
    }

    public Connection nonManagedConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException ex) {
            throw new MatrixErrorException(ex);
        }
    }

    public <T> T executeInSpringManagedConnection(Callback<T> callback) {
        Connection connection = springManagedConnection();
        return callback.execute(connection);
    }

    public <T> T executeInNonManagedConnection(Callback<T> callback) {
        return executeInNonManagedConnection(callback, Connection.TRANSACTION_REPEATABLE_READ);
    }

    public <T> T executeInNonManagedConnection(Callback<T> callback, int transactionIsolation) {
        Connection connection = nonManagedConnection();
        try {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(transactionIsolation);
        } catch (SQLException e) {
            throw new MatrixErrorException("Couldn't set 'autoCommit=false' for jdbc connection. " + e.getMessage(), e);
        }

        try {
            final T result = callback.execute(connection);
            commitConnection(connection);
            return result;
        } catch (RuntimeException e) {
            rollbackConnection(connection);
            throw new MatrixErrorException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public void closeConnection(Connection connection) {
        // Will work for transactional and non-transactional connections.
        DataSourceUtils.releaseConnection(connection, this.dataSource);
    }


    private void commitConnection(Connection connection) {
        if (null == connection) {
            return;
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new MatrixErrorException("Couldn't commit jdbc connection. " + e.getMessage(), e);
        }
    }

    private void rollbackConnection(Connection connection) {
        if (null == connection) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException e) {
            logger.error("Couldn't rollback jdbc connection. " + e.getMessage(), e);
        }
    }

    public interface Callback<T> {
        T execute(Connection connection) throws RuntimeException;
    }

}
