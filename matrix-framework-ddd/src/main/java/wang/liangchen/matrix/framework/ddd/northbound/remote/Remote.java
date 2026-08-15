package wang.liangchen.matrix.framework.ddd.northbound.remote;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark a remote service
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Remote {
    RemoteType value();
}
