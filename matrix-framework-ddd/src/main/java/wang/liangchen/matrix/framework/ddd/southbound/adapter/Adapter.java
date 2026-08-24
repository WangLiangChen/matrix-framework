package wang.liangchen.matrix.framework.ddd.southbound.adapter;

import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark a adapter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Adapter {
    PortType value();
}
