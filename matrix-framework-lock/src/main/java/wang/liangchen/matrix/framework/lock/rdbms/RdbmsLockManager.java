package wang.liangchen.matrix.framework.lock.rdbms;

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
    public Lock getLock(LockProperties lockProperties) {
        return new UpdateLockImpl(lockProperties, this.dataSource);
    }

    @Override
    public void executeInLock(LockProperties lockProperties, LockRunnable runnable) throws Throwable {
        Lock lock = getLock(lockProperties);
        boolean obtainedLock = lock.lock();
        if (!obtainedLock) {
            return;
        }
        // 获锁成功 才执行
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <R> LockResult<R> executeInLock(LockProperties lockProperties, LockSupplier<R> supplier) throws Throwable {
        Lock lock = getLock(lockProperties);
        boolean obtainedLock = lock.lock();
        // 获锁失败略过任务
        if (!obtainedLock) {
            return LockResult.skipped();
        }
        // 获锁成功
        try {
            return LockResult.newInstance(supplier.get());
        } finally {
            lock.unlock();
        }
    }
}
