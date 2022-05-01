package wang.liangchen.matrix.framework.data.dao;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.data.datasource.ConnectionsManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    private final String DELETE_SQL = StringUtil.INSTANCE.format("delete from {} where lock_key=?", IDBLock.TABLE_NAME);
    // 当前线程持有的锁
    private final static TransmittableThreadLocal<Set<String>> currentThreadLockPool = TransmittableThreadLocal.withInitial(() -> new HashSet<>(16));

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
        Connection connection = ConnectionsManager.INSTANCE.nonManagedConnection();
        try {
            boolean autoCommit = connection.getAutoCommit();
            if (autoCommit) {
                logger.debug("connection autoCommit:{}, set to false,will commit or rollback", autoCommit);
                connection.setAutoCommit(false);
                return executeInNonManagedTXLock(connection, lockKey, callback);
            }
            logger.debug("connection autoCommit:{},managed by Spring Framework", autoCommit);
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
            commitConnection(connection);
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
        logger.debug("Lock '{}' is desired", lockKey);
        // 当前线程已经持有锁
        if (currentThreadHasLock(lockKey)) {
            logger.debug("Lock '{}' is already owned", lockKey);
            return true;
        }
        // 循环执行SQL,竞争不到锁则在此阻塞；竞争成功获取到锁则加入到当前线程；
        loopExecuteLockSQL(connection, lockKey);
        currentThreadLocks().add(lockKey);
        logger.debug("Lock '{}' is given", lockKey);
        return true;
    }

    private void unlock(String lockKey, boolean obtainedLock) {
        if (!obtainedLock) {
            return;
        }
        if (currentThreadHasLock(lockKey)) {
            logger.debug("Lock '{}' is returned", lockKey);
            currentThreadLocks().remove(lockKey);
            return;
        }
        logger.warn("Lock '{}' attempt to return -- but not owner!!!", lockKey);
    }

    private void loopExecuteLockSQL(Connection connection, String lockKey) {
        SQLException lastException = null;
        // 循环执行锁定SQL,出现异常的线程延时重试
        for (int count = 1; count <= retryCount; count++) {
            logger.debug("Lock '{}' will attempt - count:{} ", lockKey, count);
            try {
                executeLockSQL(connection, lockKey);
                return;
            } catch (SQLException e) {
                // 插入冲突异常、死锁异常或者锁定超时异常
                lastException = e;
                // 达到重试次数
                if (count >= retryCount) {
                    logger.debug("Lock '{}' was not obtained - finally", lockKey);
                    break;
                }
                // 延时重试
                logger.debug("Lock '{}' was not obtained - will try again.count:{}", lockKey, count);
                try {
                    Thread.sleep(retryPeriod);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new MatrixInfoException("Failure obtaining lock, reached maximum number of attempts. Initial exception (if any) attached as root cause.{}", lastException.getMessage());
    }

    private void commitConnection(Connection connection) {
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

    private void rollbackConnection(Connection connection) {
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

    private void closeConnection(Connection connection) {
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

    protected void closeStatement(Statement statement) {
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

    protected void closeResultSet(ResultSet resultSet) {
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


    public boolean currentThreadHasLock(String lockKey) {
        return currentThreadLocks().contains(lockKey);
    }

    public Set<String> currentThreadLocks() {
        return currentThreadLockPool.get();
    }
}
