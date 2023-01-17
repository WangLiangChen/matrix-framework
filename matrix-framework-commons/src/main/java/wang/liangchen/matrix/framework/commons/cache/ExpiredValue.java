package wang.liangchen.matrix.framework.commons.cache;

import java.time.Duration;
import java.time.LocalDateTime;

class ExpiredValue<V> {
    private final V value;
    private final Duration timeout;
    private final LocalDateTime createDatetime;

    ExpiredValue(V value, Duration timeout) {
        this.value = value;
        this.timeout = timeout;
        this.createDatetime = LocalDateTime.now();
    }

    public V getValue() {
        return value;
    }

    public boolean isExpired() {
        if (timeout.isZero() || timeout.isNegative()) {
            return false;
        }
        return LocalDateTime.now().isAfter(this.createDatetime.plus(timeout));
    }
}
