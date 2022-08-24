package wang.liangchen.matrix.framework.lock.core;

import java.util.function.Supplier;

/**
 * @author Liangchen.Wang 2022-08-22 22:47
 */
public interface LockManager {
    Lock getLock(LockConfiguration lockConfiguration);

    void executeInLock(LockConfiguration lockConfiguration, Runnable task);

    <R> TaskResult<R> executeInLock(LockConfiguration lockConfiguration, Supplier<R> task);
}
