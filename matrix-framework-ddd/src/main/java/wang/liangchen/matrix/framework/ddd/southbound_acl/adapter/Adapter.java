package wang.liangchen.matrix.framework.ddd.southbound_acl.adapter;

import wang.liangchen.matrix.framework.ddd.southbound_acl.port.PortType;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark a adapter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface Adapter {
    PortType value();
}
