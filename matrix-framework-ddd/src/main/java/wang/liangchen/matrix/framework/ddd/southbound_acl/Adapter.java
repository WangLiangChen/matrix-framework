package wang.liangchen.matrix.framework.ddd.southbound_acl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @author Liangchen.Wang
 * Marker interface
 * Adapter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Adapter {
    PortType value();
}
