package com.sintrue.samples.test;

import com.sintrue.samples.api.SampleRequest;
import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.jackson.JacksonUtil;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.data.pagination.OrderBy;
import wang.liangchen.matrix.framework.data.pagination.Pagination;

import java.lang.reflect.Method;
import java.time.Duration;
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
}
