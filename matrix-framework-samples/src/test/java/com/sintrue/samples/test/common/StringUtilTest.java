package com.sintrue.samples.test.common;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.StringUtil;

public class StringUtilTest {
    @Test
    public void testEmpty() {
        String string = " ";
        System.out.println(StringUtil.INSTANCE.isNullOrEmpty(string));
        System.out.println(StringUtil.INSTANCE.isNotNullAndEmpty(string));
    }
    @Test
    public void testFormat(){
        System.out.println(StringUtil.INSTANCE.format("a{}","a","b","c"));
    }
}
