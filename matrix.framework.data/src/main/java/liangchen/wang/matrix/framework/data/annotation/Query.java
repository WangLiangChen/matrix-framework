package liangchen.wang.matrix.framework.data.annotation;


import liangchen.wang.matrix.framework.data.enumeration.AndOr;
import liangchen.wang.matrix.framework.data.enumeration.Operator;

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
