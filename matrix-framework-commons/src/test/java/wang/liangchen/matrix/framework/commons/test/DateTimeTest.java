package wang.liangchen.matrix.framework.commons.test;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Liangchen.Wang 2022-04-11 11:02
 */
public class DateTimeTest {
    @Test
    public void testZoneId() {
        ZoneId zoneId = ZoneId.systemDefault();
        System.out.println(zoneId.normalized());
    }
    @Test
    public void testTimeZone(){
        TimeZone timeZone = TimeZone.getDefault();
        System.out.println(timeZone.toZoneId().toString());
    }
    @Test
    public void testString(){
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
        String format = LocalDateTime.now().format(DATE_FORMATTER);
        System.out.println(format);
    }
}
