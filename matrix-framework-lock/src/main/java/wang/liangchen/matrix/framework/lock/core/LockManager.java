package wang.liangchen.matrix.framework.lock.core;

/**
 * @author Liangchen.Wang 2022-08-22 22:47
 */
public interface LockManager {
    Lock getLock(LockConfiguration lockConfiguration);

    void executeInLock(LockConfiguration lockConfiguration, RunnableTask task) throws Throwable;

    <R> TaskResult<R> executeInLock(LockConfiguration lockConfiguration, SupplierTask<R> task) throws Throwable;
}
