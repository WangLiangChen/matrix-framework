package com.sintrue.samples.test;

import com.sintrue.samples.api.SampleRequest;
import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.jackson.JacksonUtil;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.spring.data.pagination.OrderBy;
import wang.liangchen.matrix.framework.spring.data.pagination.Pagination;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

public class MainTest {
    @Test
    public void testOptional() {
        Optional<String> optional = Optional.ofNullable(null);
        System.out.println(optional.orElseGet(() -> "newName"));
    }

    @Test
    public void testDuration() {
        Duration duration = Duration.ZERO;
        System.out.println(Duration.ZERO == duration);
    }

    @Test
    public void testOverride() throws NoSuchMethodException {
        Method method = SampleRequest.class.getMethod("hashCode");
        System.out.println(method.getDeclaringClass());
        System.out.println(method.getDeclaringClass() == SampleRequest.class);
    }

    @Test
    public void testCreateInstance() throws InterruptedException {
        int count = 1000000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            new OrderBy();
        }
        long end = System.currentTimeMillis() - start;
        System.out.println(end);
    }

    @Test
    public void testJackson() {
        Pagination pagination = new Pagination(1, 10);
        System.out.println(JacksonUtil.INSTANCE.writeValueAsString(pagination));
    }

    @Test
    public void testCast() {
        String source = "123";
        Object o = ObjectUtil.INSTANCE.castTo(source, String.class);
        System.out.println(o);
    }

    @Test
    public void testDouble() {
        try {
            String a = null;
            System.out.println(a.toString());
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder(e.getClass().getName() + ": " + e.getMessage() + "\n");
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                stringBuilder.append(stackTraceElement.toString()).append("\n");
            }
            System.out.println(stringBuilder.toString());
        }
    }

    @Test
    public void testMove() {
    }

    @Test
    public void testMeanSolarTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2025, 3, 21, 15, 18, 0, 0, ZoneId.systemDefault());
        long eot = equationOfTime(zonedDateTime);
        zonedDateTime = meanSolarTime(zonedDateTime,116.46);
        System.out.println(zonedDateTime);

        System.out.println(zonedDateTime.plusSeconds(eot));
    }

    public ZonedDateTime meanSolarTime(ZonedDateTime zonedDateTime, double localLongitude) {
        ZoneOffset offset = zonedDateTime.getOffset();
        int offsetHours = offset.getTotalSeconds() / 3600;
        int centralLongitudeOfZone = offsetHours * 360 / 24;
        double deltaSeconds = (localLongitude - centralLongitudeOfZone) * 3600 / 15;
        return zonedDateTime.plusSeconds(Math.round(deltaSeconds));
    }


    public long equationOfTime(ZonedDateTime localDateTime) {
        ZonedDateTime utc = localDateTime.withZoneSameInstant(ZoneOffset.UTC);
        int year = utc.getYear();
        int doy = utc.getDayOfYear();
        // 计算地球在其轨道上的平近点角(Mean Anomaly)
        double d = 6.24004077 + 0.01720197 * (365.2425 * (year - 2000) + doy);
        // 地轴的倾斜(Obliquity)
        double obliquity = 9.863 * Math.sin(2 * d + 3.5932);
        // 地球轨道的偏心率(Eccentricity)
        double eccentricity = -7.659 * Math.sin(d);
        return Math.round((obliquity + eccentricity)*60);
    }
}
