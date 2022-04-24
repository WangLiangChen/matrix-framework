package wang.liangchen.matrix.framework.ddd.northbound_ohs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @author Liangchen.Wang
 * 标识远程服务
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Remote {
    RemoteType value();
}
