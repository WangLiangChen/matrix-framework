package wang.liangchen.matrix.framework.ddd.southbound_acl.port;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark a port
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface Port {
    PortType value();
}
