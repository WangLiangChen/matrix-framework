package wang.liangchen.matrix.framework.data.annotation;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang 2022-04-19 9:06
 * 标识状态字段
 * 使用该注解后，生成的代码中包含状态迁移和根据状态查询等方法
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnState {
    String value();
}
