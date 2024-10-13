package com.sintrue.samples.test;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;


public class CommonTests {
    @Test
    public void testMessageFormat() {
        String message ="hello '{}' and '{}'";
        String string = ValidationUtil.INSTANCE.resolveMessage(message, "a", "b");
        System.out.println(string);

        Student student = ClassUtil.INSTANCE.instantiate(Student.class);
        student.setStduentId(100L);
        student.setStduentName("name");
        System.out.println(student.getStduentName());
        student = ClassUtil.INSTANCE.instantiate(Student.class);
        System.out.println(student.getStduentName());

    }
}
