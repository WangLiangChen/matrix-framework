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
     * 当前线程持有的锁-多个
     */
    private final static ThreadLocal<Set<LockProperties.LockKey>> currentThreadHoldLocks = ThreadLocal.withInitial(HashSet::new);
    private final LockProperties lockProperties;
    private final LockProperties.LockKey lockKey;

    protected AbstractLock(LockProperties lockProperties) {
        this.lockProperties = ValidationUtil.INSTANCE.notNull(lockProperties);
        this.lockKey = this.lockProperties.getLockKey();
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
    public LockProperties lockProperties() {
        return this.lockProperties;
    }

    @Override
    public LockProperties.LockKey lockKey() {
        return lockKey;
    }
}
