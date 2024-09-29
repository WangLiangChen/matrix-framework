package com.sintrue.samples.test;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.StringUtil;


public class StringTests {
    @Test
    public void testMessageFormat() {
        String string = "{} - {} - a - b";
        String format = StringUtil.INSTANCE.format(string, "x", "y", "z");
        System.out.println(format);

        string = "{0} - {1} - a - b";
        format = StringUtil.INSTANCE.format(string, "x", "y", "z");
        System.out.println(format);

        string = "{s} - {t} {0}- a - b";
        format = StringUtil.INSTANCE.format(string, "x", "y", "z");
        System.out.println(format);

    }
}
