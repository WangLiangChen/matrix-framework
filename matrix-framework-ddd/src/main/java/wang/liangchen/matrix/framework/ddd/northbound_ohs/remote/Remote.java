package wang.liangchen.matrix.framework.ddd.northbound_ohs.remote;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark a remote service
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface Remote {
    RemoteType value();
}
