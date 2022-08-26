package wang.liangchen.matrix.framework.lock.rdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.lock.core.Lock;
import wang.liangchen.matrix.framework.lock.core.LockConfiguration;
import wang.liangchen.matrix.framework.lock.core.LockManager;
import wang.liangchen.matrix.framework.lock.core.TaskResult;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * @author Liangchen.Wang 2022-08-22 23:35
 */
public class RdbmsLockManager implements LockManager {
    private final static Logger logger = LoggerFactory.getLogger(RdbmsLockManager.class);
    private final DataSource dataSource;

    public RdbmsLockManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Lock getLock(LockConfiguration lockConfiguration) {
        return new UpdateLockImpl(lockConfiguration, ConnectionManager.INSTANCE.nonManagedConnection(this.dataSource));
    }

    @Override
    public void executeInLock(LockConfiguration lockConfiguration, Runnable task) {
        executeInLock(lockConfiguration, () -> {
            task.run();
            return TaskResult.skipped();
        });
    }

    @Override
    public <R> TaskResult<R> executeInLock(LockConfiguration lockConfiguration, Supplier<R> task) {
        AbstractRdbmsLock lock = (AbstractRdbmsLock) getLock(lockConfiguration);
        Connection connection = lock.getConnection();
        boolean obtainedLock = lock.lock();
        // 获锁失败略过任务
        if (!obtainedLock) {
            return TaskResult.skipped();
        }
        try {
            connection.setAutoCommit(true);
            return TaskResult.newInstance(task);
        } catch (SQLException e) {
            throw new MatrixInfoException("get Connection AutoCommit error", e);
        } finally {
            lock.unlock();
        }
    }
}
