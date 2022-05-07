package wang.liangchen.matrix.framework.commons.datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author Liangchen.Wang 2021-09-30 9:39
 * LocalDateTime LocalDate Date Instant Period ZoneId ZoneOffset ZoneRegion
 */
public enum DateTimeUtil {
    /**
     * instance
     */
    INSTANCE;
    public final static String DEFAULT_DATETIME_STRING = "yyyy-MM-dd HH:mm:ss";
    public final static String DEFAULT_DATE_STRING = "yyyy-MM-dd";
    public final static String DEFAULT_TIME_STRING = "HH:mm:ss";
    public final static DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_STRING);
    public final static DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_STRING);
    public final static DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_TIME_STRING);
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

    public Instant localDateTime2Instant(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toInstant();
    }

    public Instant localDateTime2Instant(LocalDateTime localDateTime) {
        return localDateTime2Instant(localDateTime, DEFAULT_ZONE);
    }


    public LocalDateTime ms2LocalDateTime(long ms, ZoneId zoneId) {
        Instant instant = Instant.ofEpochMilli(ms);
        return instant2LocalDateTime(instant, zoneId);
    }

    public LocalDateTime ms2LocalDateTime(long ms) {
        return ms2LocalDateTime(ms, DEFAULT_ZONE);
    }

    public long localDateTime2Ms(LocalDateTime localDateTime, ZoneId zoneId) {
        Instant instant = localDateTime2Instant(localDateTime, zoneId);
        return instant.toEpochMilli();
    }

    public long localDateTime2Ms(LocalDateTime localDateTime) {
        return localDateTime2Ms(localDateTime, DEFAULT_ZONE);
    }


    public LocalDateTime date2LocalDateTime(Date date, ZoneId zoneId) {
        Instant instant = date.toInstant();
        return instant2LocalDateTime(instant, zoneId);
    }

    public LocalDateTime date2LocalDateTime(Date date) {
        return date2LocalDateTime(date, DEFAULT_ZONE);
    }

    public Date localDateTime2Date(LocalDateTime localDateTime, ZoneId zoneId) {
        Instant instant = localDateTime2Instant(localDateTime, zoneId);
        return Date.from(instant);
    }

    public Date localDateTime2Date(LocalDateTime localDateTime) {
        Instant instant = localDateTime2Instant(localDateTime, DEFAULT_ZONE);
        return Date.from(instant);
    }


    public String format(LocalDateTime localDateTime) {
        return localDateTime.format(DEFAULT_DATETIME_FORMATTER);
    }

    public String format(LocalDate localDate) {
        return localDate.format(DEFAULT_DATE_FORMATTER);
    }

    public LocalDate dateString2LocalDate(String dateString) {
        return LocalDate.parse(dateString, DEFAULT_DATE_FORMATTER);
    }

    public LocalDateTime dateTimeString2LocalDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DEFAULT_DATETIME_FORMATTER);
    }

    public LocalTime timeString2LocalTime(String timeString) {
        return LocalTime.parse(timeString, DEFAULT_TIME_FORMATTER);
    }

}
