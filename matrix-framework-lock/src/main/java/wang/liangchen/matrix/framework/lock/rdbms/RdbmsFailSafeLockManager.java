package wang.liangchen.matrix.framework.lock.rdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.lock.core.Lock;
import wang.liangchen.matrix.framework.lock.core.LockConfiguration;
import wang.liangchen.matrix.framework.lock.core.LockManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * @author Liangchen.Wang 2022-08-22 23:35
 */
public class RdbmsFailSafeLockManager implements LockManager {
    private final static Logger logger = LoggerFactory.getLogger(RdbmsFailSafeLockManager.class);
    private final DataSource dataSource;

    public RdbmsFailSafeLockManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Lock getLock(LockConfiguration lockConfiguration) {
        return new ForUpdateLockImpl(lockConfiguration, ConnectionManager.INSTANCE.nonManagedConnection(this.dataSource));
    }

    @Override
    public void executeInLock(LockConfiguration lockConfiguration, Runnable task) {
        executeInLock(lockConfiguration, () -> {
            task.run();
            return null;
        });
    }

    @Override
    public <R> R executeInLock(LockConfiguration lockConfiguration, Supplier<R> task) {
        AbstractRdbmsLock lock = (AbstractRdbmsLock) getLock(lockConfiguration);
        Connection connection = lock.getConnection();
        try {
            boolean autoCommit = connection.getAutoCommit();
            if (autoCommit) {
                logger.debug("connection autoCommit:{}, set to false,will commit or rollback", autoCommit);
                connection.setAutoCommit(false);
                return executeInNonManagedTXLock(lock, connection, task);
            }
            logger.debug("connection autoCommit:{},managed by Spring Framework", autoCommit);
            return executeInManagedTXLock(lock, task);
        } catch (SQLException e) {
            throw new MatrixInfoException("get Connection AutoCommit error", e);
        }
    }

    private <R> R executeInNonManagedTXLock(Lock lock, Connection connection, Supplier<R> task) {
        lock.lock();
        try {
            R result = task.get();
            // 显式提交
            ConnectionManager.INSTANCE.commitConnection(connection);
            return result;
        } catch (Exception e) {
            // 显式回滚
            ConnectionManager.INSTANCE.rollbackConnection(connection);
            throw e;
        } finally {
            try {
                lock.unlock();
            } finally {
                ConnectionManager.INSTANCE.closeConnection(connection);
            }
        }
    }

    private <R> R executeInManagedTXLock(Lock lock, Supplier<R> task) {
        lock.lock();
        try {
            return task.get();
        } finally {
            lock.unlock();
        }
    }
}
