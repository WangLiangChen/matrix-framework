package wang.liangchen.matrix.framework.data.dao;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author LiangChen.Wang
 */
public abstract class AbstractDBLock implements IDBLock {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDBLock.class);
    private final static int retryCount = 3;
    private final static long retryPeriod = 500L;
    private final String DELETESQL = StringUtil.INSTANCE.format("delete from {} where lock_key=?", IDBLock.TABLE_NAME);
    // 持有锁的线程
    private final static TransmittableThreadLocal<Set<String>> lockOwnerThreads = TransmittableThreadLocal.withInitial(() -> new HashSet<>(16));

    @Inject
    private DataSource dataSource;

    protected Logger getLogger() {

        return logger;
    }

    protected abstract void executeLockSQL(Connection connection, String lockKey) throws SQLException;


    @Override
    public void executeInLock(String lockKey, Runnable callback) {
        executeInLock(lockKey, () -> {
            callback.run();
            return null;
        });
    }

    @Override
    public <R> R executeInLock(String lockKey, Supplier<R> callback) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            boolean autoCommit = connection.getAutoCommit();
            if (autoCommit) {
                getLogger().debug("connection autoCommit:{}, 设置为false并显式提交", autoCommit);
                connection.setAutoCommit(false);
                return executeInNonManagedTXLock(connection, lockKey, callback);
            }
            getLogger().debug("connection autoCommit:{}, spring管理的连接事务，spring会做事务的提交或者回滚", autoCommit);
            return executeInManagedTXLock(connection, lockKey, callback);
        } catch (SQLException e) {
            throw new MatrixInfoException("getConnection error", e);
        }
    }


    private <R> R executeInNonManagedTXLock(Connection connection, String lockKey, Supplier<R> callback) {
        boolean obtainedLock = false;
        try {
            if (StringUtil.INSTANCE.isNotBlank(lockKey)) {
                obtainedLock = lock(connection, lockKey);
            }
            final R result = callback.get();
            try {
                commitConnection(connection);
            } catch (SQLException e) {
                rollbackConnection(connection);
            }
            return result;
        } catch (Exception e) {
            rollbackConnection(connection);
            throw e;
        } finally {
            try {
                unlock(lockKey, obtainedLock);
            } finally {
                closeConnection(connection);
            }
        }
    }

    private <R> R executeInManagedTXLock(Connection connection, String lockKey, Supplier<R> callback) {
        boolean obtainedLock = false;
        try {
            if (StringUtil.INSTANCE.isNotBlank(lockKey)) {
                obtainedLock = lock(connection, lockKey);
            }
            return callback.get();
        } finally {
            unlock(lockKey, obtainedLock);
        }
    }

    private boolean lock(Connection connection, String lockKey) {
        logger.debug("Lock '{}' is desired by: {}", lockKey, Thread.currentThread().getName());
        // 当前线程存在，则认为当前线程持有锁
        if (isLockOwnerThread(lockKey)) {
            logger.debug("Lock '{}' is already owned by: {}", lockKey, Thread.currentThread().getName());
            return true;
        }
        // 循环执行SQL，执行成功获取到锁则加入到当前线程；获取不到锁则在此等待。
        loopExecuteLockSQL(connection, lockKey);
        getLockOwnerThreads().add(lockKey);
        logger.debug("Lock '{}' is given to: {}", lockKey, Thread.currentThread().getName());
        return true;
    }

    private void unlock(String lockKey, boolean obtainedLock) {
        if (!obtainedLock) {
            return;
        }
        if (isLockOwnerThread(lockKey)) {
            logger.debug("Lock '{}' is returned by:{}", lockKey, Thread.currentThread().getName());
            getLockOwnerThreads().remove(lockKey);
            // 删除对应的数据
            // deleteKey(lockKey);
        } else {
            logger.warn("Lock '" + lockKey + "' attempt to return by: " + Thread.currentThread().getName() + " -- but not owner!", new Exception("stack-trace of wrongful returner"));
        }
    }

    private void loopExecuteLockSQL(Connection connection, String lockKey) {
        SQLException lastException = null;
        // 循环执行SQL，不成功则重试，或超过次数异常
        for (int count = 0; count < retryCount; count++) {
            try {
                executeLockSQL(connection, lockKey);
                return;
            } catch (SQLException e) {
                // 插入冲突异常，死锁异常或者锁定超时异常
                lastException = e;
                if ((count + 1) == retryCount) {
                    getLogger().debug("Lock '{}' was not obtained by: {}", lockKey, Thread.currentThread().getName());
                } else {
                    getLogger().debug("Lock '{}' was not obtained by: {} - will try again.", lockKey, Thread.currentThread().getName());
                }
                try {
                    Thread.sleep(retryPeriod);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new MatrixInfoException("Failure obtaining db row lock, reached maximum number of attempts. Initial exception (if any) attached as root cause.{}", lastException.getMessage());
    }

    private void commitConnection(Connection connection) throws SQLException {
        if (null == connection) {
            return;
        }
        connection.commit();
    }

    private void rollbackConnection(Connection connection) {
        if (null == connection) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException e) {
            getLogger().error("Couldn't rollback jdbc connection. ", e);
        }
    }

    private void closeConnection(Connection connection) {
        if (null == connection) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            getLogger().error("Failed to close Connection", e);
        } catch (Throwable e) {
            getLogger().error("Unexpected exception closing Connection.This is often due to a Connection being returned after or during shutdown.", e);
        }
    }

    protected void closeStatement(Statement statement) throws SQLException {
        if (null == statement) {
            return;
        }
        statement.close();
    }

    protected void closeResultSet(ResultSet resultSet) throws SQLException {
        if (null == resultSet) {
            return;
        }
        resultSet.close();
    }


    public boolean isLockOwnerThread(String lockKey) {
        return getLockOwnerThreads().contains(lockKey);
    }

    private Set<String> getLockOwnerThreads() {
        return lockOwnerThreads.get();
    }

    private void deleteKey(String lockKey) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETESQL)) {
            preparedStatement.setString(1, lockKey);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            logger.error("delete lock_key:{} error:{}", lockKey, e.getMessage());
        }
    }

}
