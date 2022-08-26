package wang.liangchen.matrix.framework.lock.rdbms;

import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.lock.core.*;

import javax.sql.DataSource;

/**
 * @author Liangchen.Wang 2022-08-22 23:35
 */
public class RdbmsLockManager implements LockManager {
    private final DataSource dataSource;

    public RdbmsLockManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Lock getLock(LockConfiguration lockConfiguration) {
        return new UpdateLockImpl(lockConfiguration, ConnectionManager.INSTANCE.nonManagedConnection(this.dataSource));
    }

    @Override
    public void executeInLock(LockConfiguration lockConfiguration, RunnableTask task) throws Throwable {
        executeInLock(lockConfiguration, () -> {
            task.run();
            return TaskResult.skipped();
        });
    }

    @Override
    public <R> TaskResult<R> executeInLock(LockConfiguration lockConfiguration, SupplierTask<R> task) throws Throwable {
        AbstractRdbmsLock lock = (AbstractRdbmsLock) getLock(lockConfiguration);
        boolean obtainedLock = lock.lock();
        // 获锁失败略过任务
        if (!obtainedLock) {
            return TaskResult.skipped();
        }
        try {
            return TaskResult.newInstance(task.get());
        } finally {
            lock.unlock();
        }
    }
}
