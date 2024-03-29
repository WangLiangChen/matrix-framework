package wang.liangchen.matrix.framework.data.annotation;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang 2022-04-19 9:06
 * 标识逻辑删除的属性
 * 使用该注解后，删除操作将会是逻辑删除
 * 删除的标识值为该注解的value
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnMarkDelete {
    String value();
}
