package wang.liangchen.matrix.framework.lock.core;

import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

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

    public LockConfiguration(String lockKey, Instant lockAt, Duration lockAtLeast, Duration lockAtMost) {
        this.lockKey = ValidationUtil.INSTANCE.notEmpty(lockKey);
        this.lockAt = ValidationUtil.INSTANCE.notNull(lockAt);
        this.lockAtLeast = this.lockAt.plus(ValidationUtil.INSTANCE.notNull(lockAtLeast));
        this.lockAtMost = this.lockAt.plus(ValidationUtil.INSTANCE.notNull(lockAtMost));
        ValidationUtil.INSTANCE.isFalse(ExceptionLevel.WARN,lockAtLeast.isNegative(), "lockAtLeast is negative, {}", this.lockKey);
        ValidationUtil.INSTANCE.isFalse(ExceptionLevel.WARN,lockAtLeast.compareTo(lockAtMost) > 0, "lockAtLeast is longer than lockAtMost for lock {}", this.lockKey);
    }

    public Instant getUnLockInstant() {
        Instant now = Instant.now();
        return lockAtLeast.isBefore(now) ? lockAtLeast : now;
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
