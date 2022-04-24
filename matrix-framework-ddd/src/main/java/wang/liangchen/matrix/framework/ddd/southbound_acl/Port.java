package wang.liangchen.matrix.framework.ddd.southbound_acl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @author Liangchen.Wang
 * 标识端口
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Port {
    PortType value();
}
