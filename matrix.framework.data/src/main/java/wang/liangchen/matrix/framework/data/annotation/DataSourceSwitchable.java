package wang.liangchen.matrix.framework.data.annotation;

import java.lang.annotation.*;

/**
 * @author LiangChen.Wang
 * 用于指定是否开启数据源切换
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface DataSourceSwitchable {
}
