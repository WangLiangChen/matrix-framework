package wang.liangchen.matrix.framework.data.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.springboot.context.BeanContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author LiangChen.Wang 2021/6/9
 */
public enum ConnectionManager {
    /**
     * instance
     */
    INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DataSource dataSource = BeanContext.INSTANCE.getBean("dataSource");

    public Connection springManagedConnection() {
        return springManagedConnection(this.dataSource);
    }

    public Connection springManagedConnection(DataSource dataSource) {
        try {
            return DataSourceUtils.doGetConnection(dataSource);
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

    public <T> T executeInSpringManagedConnection(DataSource dataSource, Function<Connection, T> function) {
        Connection connection = springManagedConnection(dataSource);
        return function.apply(connection);
    }

    public void executeInSpringManagedConnection(DataSource dataSource, Consumer<Connection> consumer) {
        Connection connection = springManagedConnection(dataSource);
        consumer.accept(connection);
    }


    public <T> T executeInSpringManagedConnection(Function<Connection, T> function) {
        return executeInSpringManagedConnection(this.dataSource, function);
    }

    public void executeInSpringManagedConnection(Consumer<Connection> consumer) {
        executeInSpringManagedConnection(this.dataSource, consumer);
    }

    public <T> T executeInNonManagedConnection(DataSource dataSource, Function<Connection, T> function, int transactionIsolation) {
        Connection connection = nonManagedConnection(dataSource);
        try {
            connection.setAutoCommit(false);
            if (Connection.TRANSACTION_NONE != transactionIsolation) {
                connection.setTransactionIsolation(transactionIsolation);
            }
        } catch (SQLException e) {
            throw new MatrixErrorException("Couldn't set 'autoCommit=false' for jdbc connection. " + e.getMessage(), e);
        }

        try {
            final T result = function.apply(connection);
            commitConnection(connection);
            return result;
        } catch (RuntimeException e) {
            rollbackConnection(connection);
            throw new MatrixErrorException(e);
        } finally {
            closeConnection(connection, this.dataSource);
        }
    }

    public <T> T executeInNonManagedConnection(DataSource dataSource, Function<Connection, T> function) {
        return executeInNonManagedConnection(dataSource, function, Connection.TRANSACTION_NONE);
    }

    public <T> T executeInNonManagedConnection(Function<Connection, T> function) {
        return executeInNonManagedConnection(this.dataSource, function);
    }

    public void executeInNonManagedConnection(DataSource dataSource, Consumer<Connection> consumer, int transactionIsolation) {
        executeInNonManagedConnection(dataSource, connection -> {
            consumer.accept(connection);
            return null;
        }, transactionIsolation);
    }

    public void executeInNonManagedConnection(DataSource dataSource, Consumer<Connection> consumer) {
        executeInNonManagedConnection(dataSource, consumer, Connection.TRANSACTION_NONE);
    }

    public void executeInNonManagedConnection(Consumer<Connection> consumer) {
        executeInNonManagedConnection(this.dataSource, consumer);
    }


    public void disableAutoCommit(Connection connection) {
        if (null == connection) {
            return;
        }
        try {
            boolean autoCommit = connection.getAutoCommit();
            if (autoCommit) {
                connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            logger.error("Couldn't disableAutoCommit jdbc connection. ", e);
        }
    }

    public void enableAutoCommit(Connection connection) {
        if (null == connection) {
            return;
        }
        try {
            boolean autoCommit = connection.getAutoCommit();
            if (autoCommit) {
                return;
            }
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            logger.error("Couldn't enableAutoCommit jdbc connection. ", e);
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

}
