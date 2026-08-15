package wang.liangchen.matrix.framework.ddd.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark a domain model with DomainMetaModel
 * <p>
 * 注意：@Inherited 不适用于接口，实现类上的标注不会被继承；业务类必须自行标注本注解。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainModel {
    DomainMetaModel value();
}