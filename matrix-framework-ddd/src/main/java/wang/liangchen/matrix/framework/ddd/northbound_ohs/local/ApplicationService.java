package wang.liangchen.matrix.framework.ddd.northbound_ohs.local;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * Mark a application service
 * 应用服务,不包含领域逻辑的业务服务,包含消息验证、错误处理、监控、日志、事务、访问控制等横切关注点
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface ApplicationService {
    ApplicationServiceType value() default ApplicationServiceType.NONE;
}
