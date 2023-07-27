package wang.liangchen.matrix.framework.lock.core;

import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.StringJoiner;


/**
 * @author Liangchen.Wang 2022-08-22 11:35
 */
public class LockProperties {
    private final LockKey lockKey;
    private final Instant lockAt;
    private final Instant lockAtLeast;
    private final Instant lockAtMost;

    public LockProperties(LockKey lockKey, Instant lockAt, Duration lockAtLeast, Duration lockAtMost) {
        this.lockKey = ValidationUtil.INSTANCE.notNull(lockKey);
        this.lockAt = ValidationUtil.INSTANCE.notNull(lockAt);
        this.lockAtLeast = this.lockAt.plus(ValidationUtil.INSTANCE.notNull(lockAtLeast));
        this.lockAtMost = this.lockAt.plus(ValidationUtil.INSTANCE.notNull(lockAtMost));
        ValidationUtil.INSTANCE.isFalse(ExceptionLevel.WARN, lockAtLeast.isNegative(), "lockAtLeast is negative, {}", this.lockKey);
        ValidationUtil.INSTANCE.isFalse(ExceptionLevel.WARN, lockAtLeast.compareTo(lockAtMost) > 0, "lockAtLeast is longer than lockAtMost for lock {}", this.lockKey);
    }

    public Instant getUnLockInstant() {
        Instant now = Instant.now();
        return lockAtLeast.isAfter(now) ? lockAtLeast : now;
    }

    public LockKey getLockKey() {
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

    public static class LockKey {
        private final String lockGroup;
        private final String lockKey;

        private LockKey(String lockGroup, String lockKey) {
            this.lockGroup = ValidationUtil.INSTANCE.notBlank(lockGroup);
            this.lockKey = ValidationUtil.INSTANCE.notBlank(lockKey);
        }

        public static LockKey newLockKey(String lockGroup, String lockKey) {
            return new LockKey(lockGroup, lockKey);
        }

        public String getLockGroup() {
            return lockGroup;
        }

        public String getLockKey() {
            return lockKey;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", LockKey.class.getSimpleName() + "[", "]")
                    .add("lockGroup='" + lockGroup + "'")
                    .add("lockKey='" + lockKey + "'")
                    .toString();
        }
    }
}
