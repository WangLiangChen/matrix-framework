package wang.liangchen.matrix.framework.ddd.northbound.local;

/**
 * @author Liangchen.Wang
 * Marker interface
 * Mark a application service
 * 应用服务,不包含领域逻辑的业务服务,包含消息验证、错误处理、监控、日志、事务、访问控制等横切关注点
 * 基接口不携带@ApplicationService注解（与IRemote/IPort/IAdapter基类对称），
 * 由业务应用服务类自行添加@ApplicationService(ApplicationServiceType.xxx)标注。
 */
public interface IApplicationService {
}
