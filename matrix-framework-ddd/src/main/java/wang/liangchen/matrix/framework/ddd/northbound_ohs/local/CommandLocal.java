package wang.liangchen.matrix.framework.ddd.northbound_ohs.local;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Liangchen.Wang
 * Marker interface
 * Local Service And ApplicationService
 * 标识应用服务,不包含领域逻辑的业务服务
 * 消息验证、错误处理、监控、日志、事务、访问控制等横切关注点
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface CommandLocal {

}