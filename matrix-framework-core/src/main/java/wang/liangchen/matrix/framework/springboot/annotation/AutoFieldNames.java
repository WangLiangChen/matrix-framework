package wang.liangchen.matrix.framework.springboot.annotation;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang
 * 与@Bean同用,可以覆盖同名的bean
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface AutoFieldNames {
}
