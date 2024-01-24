package wang.liangchen.matrix.framework.ddd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark a BoundedContext Package
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface BoundedContext {
    String name();

    DomainType domainType() default DomainType.Core;
}