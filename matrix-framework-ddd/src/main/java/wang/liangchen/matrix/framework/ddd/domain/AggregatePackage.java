package wang.liangchen.matrix.framework.ddd.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to specify the package of an aggregate.
 *
 * @author Liangchen Wang
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface AggregatePackage {
    /**
     * Specifies the name or identifier of an aggregate.
     * Usually the name of the aggregated-root entity.
     */
    String value();
}
