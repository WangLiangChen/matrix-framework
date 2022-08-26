package wang.liangchen.matrix.framework.lock.aop.advisor;

import wang.liangchen.matrix.framework.lock.core.LockConfiguration;
import wang.liangchen.matrix.framework.lock.core.LockManager;
import wang.liangchen.matrix.framework.lock.resolver.LockConfigurationResolver;
import wang.liangchen.matrix.framework.springboot.aop.advisor.TaskSchedulerInterceptor;

/**
 * @author Liangchen.Wang 2022-08-26 14:47
 */
public class LockableTaskSchedulerInterceptor extends TaskSchedulerInterceptor {
    private final LockManager lockManager;

    public LockableTaskSchedulerInterceptor(LockManager lockManager) {
        this.lockManager = lockManager;
    }

    @Override
    protected Runnable wrapRunnable(Runnable runnable) throws Throwable {
        LockConfiguration lockConfiguration = LockConfigurationResolver.INSTANCE.resolve(runnable);
        if (null == lockConfiguration) {
            return runnable;
        }
        return () -> {
            try {
                lockManager.executeInLock(lockConfiguration, () -> runnable);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }
}
