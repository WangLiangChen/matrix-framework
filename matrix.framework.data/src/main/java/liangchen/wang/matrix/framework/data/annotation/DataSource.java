package liangchen.wang.matrix.framework.data.annotation;

import java.lang.annotation.*;

/**
 * @author LiangChen.Wang
 * 用于指定数据源
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Documented
public @interface DataSource {
    String value();
}
