package liangchen.wang.matrix.framework.data.annotation;

import java.lang.annotation.*;

/**
 * @author LiangChen.Wang
 * 使用注解切换数据源时 指定数据源
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Documented
public @interface DataSource {
    String value();
}
