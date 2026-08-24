package wang.liangchen.matrix.framework.ddd.northbound.local;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark a application service
 * 应用服务,不包含领域逻辑的业务服务,包含消息验证、错误处理、监控、日志、事务、访问控制等横切关注点
 * <p>
 * 注意：本注解无@Inherited——接口上的注解不会传播给实现类，具体应用服务类必须自行标注
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationService {
    ApplicationServiceType value();
}
