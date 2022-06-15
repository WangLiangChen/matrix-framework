package wang.liangchen.matrix.framework.data.annotation;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang 2022-04-19 9:06
 * 标识主键列 可以指定主键生成策略 默认为自增
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {
    IdStrategy value();
}

