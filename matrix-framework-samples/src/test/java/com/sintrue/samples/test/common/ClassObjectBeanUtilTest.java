package com.sintrue.samples.test.common;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

public class ClassObjectBeanUtilTest {
    @Test
    public void testType() {
        System.out.println(byte.class == Byte.class);
        System.out.println(char.class == Character.class);
    }

    @Test
    public void testCast() {
        String b = "123";
        Long target = ObjectUtil.INSTANCE.castTo(b, Long.class);
        System.out.println(target);
    }
}
