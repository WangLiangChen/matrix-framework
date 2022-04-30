package wang.liangchen.matrix.framework.ddd;

import wang.liangchen.matrix.framework.ddd.domain.DomainType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Liangchen.Wang
 * 标识限界上下文
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface BoundedContext {
    String name();

    DomainType domainType() default DomainType.Core;
}