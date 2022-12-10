package wang.liangchen.matrix.framework.lock.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-08-22 22:44
 */
public abstract class AbstractLock implements Lock {
    private final static Logger logger = LoggerFactory.getLogger(AbstractLock.class);
    /**
     * 当前线程持有的锁
     */
    private final static ThreadLocal<Set<LockConfiguration.LockKey>> currentThreadHoldLocks = ThreadLocal.withInitial(HashSet::new);
    private final LockConfiguration lockConfiguration;
    private final LockConfiguration.LockKey lockKey;

    protected AbstractLock(LockConfiguration lockConfiguration) {
        this.lockConfiguration = ValidationUtil.INSTANCE.notNull(lockConfiguration);
        this.lockKey = this.lockConfiguration.getLockKey();
    }

    @Override
    public boolean lock() {
        logger.debug("Lock '{}' is desired", lockKey);
        if (currentThreadHoldLocks.get().contains(lockKey)) {
            logger.debug("Current Thread already holds lock '{}'", lockKey);
            return true;
        }
        boolean obtainedLock = doLock();
        if (obtainedLock) {
            currentThreadHoldLocks.get().add(lockKey);
            logger.debug("Lock '{}' is given", lockKey);
        }
        return obtainedLock;
    }

    @Override
    public void unlock() {
        currentThreadHoldLocks.get().remove(lockKey);
        logger.debug("Lock '{}' is returned", lockKey);
        doUnlock();
    }

    protected abstract boolean doLock();

    protected abstract void doUnlock();

    @Override
    public LockConfiguration lockConfiguration() {
        return this.lockConfiguration;
    }

    @Override
    public LockConfiguration.LockKey lockKey() {
        return lockKey;
    }
}
