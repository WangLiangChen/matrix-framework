package liangchen.wang.matrix.framework.commons.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Liangchen.Wang 2021-09-30 9:39
 */
public enum DateTimeUtil {
    INSTANCE;

    public LocalDateTime ms2LocalDateTime(long ms) {
        Instant instant = Instant.ofEpochMilli(ms);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
