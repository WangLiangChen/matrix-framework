package wang.liangchen.matrix.framework.springboot.annotation;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang
 * 用于覆盖context中同名的Bean
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OverrideBean {
    String value();
}
