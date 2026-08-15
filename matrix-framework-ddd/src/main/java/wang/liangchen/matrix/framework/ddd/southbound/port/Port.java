package wang.liangchen.matrix.framework.ddd.southbound.port;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark a port
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Port {
    PortType value();
}
