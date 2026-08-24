package wang.liangchen.matrix.framework.ddd.northbound.remote;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark a remote service
 * <p>
 * 注意：本注解无@Inherited——接口上的注解不会传播给实现类，具体远程服务类必须自行标注
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Remote {
    RemoteType value();
}
