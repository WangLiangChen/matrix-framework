package wang.liangchen.matrix.framework.lock.core;

import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

import java.time.Duration;
import java.time.Instant;


/**
 * @author Liangchen.Wang 2022-08-22 11:35
 */
public class LockConfiguration {
    private final String lockKey;
    private final Instant lockAt;
    private final Instant lockAtLeast;
    private final Instant lockAtMost;

    public LockConfiguration(String lockKey, Duration lockAtLeast, Duration lockAtMost) {
        this.lockKey = ObjectUtil.INSTANCE.validateNotEmpty(lockKey);
        this.lockAt = Instant.now();
        this.lockAtLeast = this.lockAt.plus(ObjectUtil.INSTANCE.validateNotNull(lockAtLeast));
        this.lockAtMost = this.lockAt.plus(ObjectUtil.INSTANCE.validateNotNull(lockAtMost));
        Assert.INSTANCE.isFalse(lockAtLeast.isNegative(), "lockAtLeast is negative, {}", this.lockKey);
        Assert.INSTANCE.isFalse(lockAtLeast.compareTo(lockAtMost) > 0, "lockAtLeast is longer than lockAtMost for lock {}", this.lockKey);
    }

    public Instant getUnLockInstant() {
        Instant now = Instant.now();
        return lockAtLeast.isAfter(now) ? lockAtLeast : now;
    }

    public String getLockKey() {
        return lockKey;
    }

    public Instant getLockAt() {
        return lockAt;
    }

    public Instant getLockAtLeast() {
        return lockAtLeast;
    }

    public Instant getLockAtMost() {
        return lockAtMost;
    }
}
