package wang.liangchen.matrix.framework.ddd.domain.identity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * 标注实体的身份标识字段：该字段的类型应为IIdentity（身份标识值对象）。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Identity {
}
