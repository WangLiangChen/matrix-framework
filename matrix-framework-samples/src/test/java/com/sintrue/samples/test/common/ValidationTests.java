package com.sintrue.samples.test.common;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.runtime.MessageWrapper;

public class ValidationTests {
    @Test
    public void testResolveMessage(){
        MessageWrapper messageWrapper = MessageWrapper.of("{a.b.c.d.f}").withCode("111");
        System.out.println();
    }
}
