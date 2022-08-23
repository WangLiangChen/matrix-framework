package wang.liangchen.matrix.framework.data.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.springboot.context.BeanLoader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author LiangChen.Wang 2021/6/9
 */
public enum ConnectionManager {
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
        return nonManagedConnection(this.dataSource);
    }

    public Connection nonManagedConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
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
            closeConnection(connection, this.dataSource);
        }
    }

    public void commitConnection(Connection connection) {
        if (null == connection) {
            return;
        }
        try {
            if (connection.isClosed()) {
                logger.warn("Connection is closed!");
                return;
            }
            connection.commit();
        } catch (SQLException e) {
            logger.error("Couldn't commit jdbc connection. ", e);
        }
    }

    public void rollbackConnection(Connection connection) {
        if (null == connection) {
            return;
        }
        try {
            if (connection.isClosed()) {
                logger.warn("Connection is closed!");
                return;
            }
            connection.rollback();
        } catch (SQLException e) {
            logger.error("Couldn't rollback jdbc connection. ", e);
        }
    }

    public void closeConnection(Connection connection, DataSource dataSource) {
        // Will work for transactional and non-transactional connections.
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

    public void closeConnection(Connection connection) {
        if (null == connection) {
            return;
        }
        try {
            if (connection.isClosed()) {
                logger.warn("Connection is closed!");
                return;
            }
            connection.close();
        } catch (SQLException e) {
            logger.error("Failed to close Connection", e);
        } catch (Exception e) {
            logger.error("Unexpected exception closing Connection.This is often due to a Connection being returned after or during shutdown.", e);
        }
    }

    public void closeStatement(Statement statement) {
        if (null == statement) {
            return;
        }
        try {
            if (statement.isClosed()) {
                logger.warn("Statement is closed!");
                return;
            }
            statement.close();
        } catch (SQLException e) {
            logger.error("Failed to close Statement", e);
        }
    }

    public void closeResultSet(ResultSet resultSet) {
        if (null == resultSet) {
            return;
        }
        try {
            if (resultSet.isClosed()) {
                logger.warn("ResultSet is closed!");
                return;
            }
            resultSet.close();
        } catch (SQLException e) {
            logger.error("Failed to close ResultSet", e);
        }
    }

    public interface Callback<T> {
        T execute(Connection connection) throws RuntimeException;
    }

}
