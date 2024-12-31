package wang.liangchen.matrix.framework.data.annotation;


import java.lang.annotation.*;

/**
 * @author Liangchen.Wang 2022-04-19 9:06
 * 标识逻辑删除的属性
 * 使用该注解后，删除操作将会是逻辑删除
 * 删除的标识值为该注解的value
 * 标注的属性类型，必须可以从String转型得到
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnSoftDelete {
    String value();

    Class<?> type() default String.class;
}
