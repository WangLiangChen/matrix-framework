package wang.liangchen.matrix.framework.lock.core;

/**
 * @author Liangchen.Wang 2022-08-22 22:47
 */
public interface LockManager {
    Lock getLock(LockProperties lockProperties);

    void executeInLock(LockProperties lockProperties, LockRunnable runnable) throws Throwable;

    <R> LockResult<R> executeInLock(LockProperties lockProperties, LockSupplier<R> supplier) throws Throwable;
}
