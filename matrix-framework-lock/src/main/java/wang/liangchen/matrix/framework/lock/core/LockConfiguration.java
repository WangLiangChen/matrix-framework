package wang.liangchen.matrix.framework.lock.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

import java.time.Duration;
import java.time.Instant;


/**
 * @author Liangchen.Wang 2022-08-22 11:35
 */
public class LockConfiguration {
    private final static Logger logger = LoggerFactory.getLogger(LockConfiguration.class);
    private final String lockKey;
    private final Instant lockAt;
    private final Duration lockAtLeast;
    private final Duration lockAtMost;

    public LockConfiguration(String lockKey, Instant lockAt, Duration lockAtLeast, Duration lockAtMost) {
        this.lockKey = ObjectUtil.INSTANCE.validateNotEmpty(lockKey);
        this.lockAt = ObjectUtil.INSTANCE.validateNotNull(lockAt);
        this.lockAtLeast = ObjectUtil.INSTANCE.validateNotNull(lockAtLeast);
        this.lockAtMost = ObjectUtil.INSTANCE.validateNotNull(lockAtMost);
        Assert.INSTANCE.isFalse(lockAtLeast.isNegative(), "lockAtLeast is negative, {}", this.lockKey);
        Assert.INSTANCE.isFalse(lockAtLeast.compareTo(lockAtMost) > 0, "lockAtLeast is longer than lockAtMost for lock {}", this.lockKey);
    }

    public Instant getLockAtLeastExpire() {
        return lockAt.plus(lockAtLeast);
    }

    public Instant getLockAtMostExpire() {
        return lockAt.plus(lockAtMost);
    }

    public Instant getUnLockInstant() {
        Instant now = Instant.now();
        Instant lockAtLeastExpire = getLockAtLeastExpire();
        return lockAtLeastExpire.isAfter(now) ? lockAtLeastExpire : now;
    }

    public String getLockKey() {
        return lockKey;
    }

    public Instant getLockAt() {
        return lockAt;
    }

    public Duration getLockAtLeast() {
        return lockAtLeast;
    }

    public Duration getLockAtMost() {
        return lockAtMost;
    }
}
