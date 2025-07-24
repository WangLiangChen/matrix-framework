package wang.liangchen.matrix.framework.spring.data.annotation;

import java.lang.annotation.*;

/**
 * @author LiangChen.Wang
 * 用于切换数据源
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Documented
public @interface DataSourceRouter {
    String value();
}
