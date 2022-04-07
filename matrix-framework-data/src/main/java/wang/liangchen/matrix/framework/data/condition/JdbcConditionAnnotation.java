package wang.liangchen.matrix.framework.data.condition;


import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * @author LiangChen.Wang 2019/9/17 20:40
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(JdbcCondition.class)
public @interface JdbcConditionAnnotation {
}
