package wang.liangchen.matrix.framework.springboot.annotation;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OverrideBean {
    String value();
}
