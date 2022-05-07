package wang.liangchen.matrix.framework.commons.datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author Liangchen.Wang 2021-09-30 9:39
 * LocalDateTime LocalDate Date Instant ZoneId
 */
public enum DateTimeUtil {
    /**
     * instance
     */
    INSTANCE;
    public final static String DATETIME_STRING = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_STRING = "yyyy-MM-dd";
    public final static String TIME_STRING = "HH:mm:ss";
    public final static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_STRING);
    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_STRING);
    public final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_STRING);

    public final static ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    public final static ZoneId CN_ZONE = ZoneId.of("Asia/Shanghai");
    public final static ZoneOffset DEFAULT_ZONE_OFFSET = ZonedDateTime.now(DEFAULT_ZONE).getOffset();
    public final static ZoneOffset CN_ZONE_OFFSET = ZonedDateTime.now(CN_ZONE).getOffset();


    public LocalDateTime instant2LocalDateTime(Instant instant, ZoneId zoneId) {
        return LocalDateTime.ofInstant(instant, zoneId);
    }

    public LocalDateTime instant2LocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, DEFAULT_ZONE);
    }

    public Instant localDateTime2Instant(LocalDateTime localDateTime, ZoneOffset zoneOffset) {
        return localDateTime.toInstant(zoneOffset);
    }

    public Instant localDateTime2Instant(LocalDateTime localDateTime) {
        return localDateTime2Instant(localDateTime, DEFAULT_ZONE_OFFSET);
    }


    public LocalDateTime ms2LocalDateTime(long ms, ZoneId zoneId) {
        Instant instant = Instant.ofEpochMilli(ms);
        return instant2LocalDateTime(instant, zoneId);
    }

    public LocalDateTime ms2LocalDateTime(long ms) {
        return ms2LocalDateTime(ms, DEFAULT_ZONE);
    }


    public LocalDateTime date2LocalDateTime(Date date, ZoneId zoneId) {
        Instant instant = date.toInstant();
        return instant2LocalDateTime(instant, zoneId);
    }

    public LocalDateTime date2LocalDateTime(Date date) {
        return date2LocalDateTime(date, DEFAULT_ZONE);
    }

    public String format(LocalDateTime localDateTime) {
        return localDateTime.format(DATETIME_FORMATTER);
    }

    public LocalDate dateString2LocalDate(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

}
