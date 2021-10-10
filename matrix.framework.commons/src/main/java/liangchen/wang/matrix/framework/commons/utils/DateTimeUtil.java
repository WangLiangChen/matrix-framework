package liangchen.wang.matrix.framework.commons.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author Liangchen.Wang 2021-09-30 9:39
 */
public enum DateTimeUtil {
    INSTANCE;
    public final static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("GMT+08:00");

    public LocalDateTime ms2LocalDateTime(long ms) {
        Instant instant = Instant.ofEpochMilli(ms);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
