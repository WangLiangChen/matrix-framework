package com.sintrue.samples.test;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import javax.sql.DataSource;
import java.util.Locale;

@SpringBootTest
public class MatrixFrameworkTests {
    @Inject
    private DataSource dataSource;
    @Test
    public void testValidation() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        String s = ValidationUtil.INSTANCE.resolveMessage("a-{System.Warn}-b");
        System.out.println(s);
    }

}
