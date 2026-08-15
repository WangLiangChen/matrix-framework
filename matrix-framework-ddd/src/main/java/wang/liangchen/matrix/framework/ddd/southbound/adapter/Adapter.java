package wang.liangchen.matrix.framework.ddd.southbound.adapter;

import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark a adapter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Adapter {
    PortType value();
}
