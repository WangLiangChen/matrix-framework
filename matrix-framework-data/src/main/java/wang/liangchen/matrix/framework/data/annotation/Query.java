package wang.liangchen.matrix.framework.data.annotation;


import wang.liangchen.matrix.framework.data.query.AndOr;
import wang.liangchen.matrix.framework.data.query.Operator;

import java.lang.annotation.*;

/**
 * @author .LiangChen.Wang
 * 用于自动构建SQL时的查询条件
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Query {
    AndOr andOr() default AndOr.AND;

    Operator operator();

    String column() default "";

    String group() default "";
}
