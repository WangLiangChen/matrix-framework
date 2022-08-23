package wang.liangchen.matrix.framework.lock.core;

import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

/**
 * @author Liangchen.Wang 2022-08-22 22:44
 */
public abstract class AbstractLock implements Lock {
    private final LockConfiguration lockConfiguration;

    protected AbstractLock(LockConfiguration lockConfiguration) {
        this.lockConfiguration = ObjectUtil.INSTANCE.validateNotNull(lockConfiguration);
    }

    @Override
    public boolean lock() {
        return doLock();
    }

    @Override
    public void unlock() {
        doUnlock();
    }

    protected abstract boolean doLock();

    protected abstract void doUnlock();

    @Override
    public LockConfiguration lockConfiguration() {
        return this.lockConfiguration;
    }
}
