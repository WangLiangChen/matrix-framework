package wang.liangchen.matrix.framework.ddd.domain.aggregate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Liangchen Wang
 * Marker annotation to specify the package of an aggregate.
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AggregatePackage {
    /**
     * Specifies the name or identifier of an aggregate.
     * Usually the name of the aggregated-root entity.
     */
    String name();
}
