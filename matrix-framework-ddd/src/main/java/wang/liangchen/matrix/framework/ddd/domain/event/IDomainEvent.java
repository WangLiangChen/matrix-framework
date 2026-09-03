package wang.liangchen.matrix.framework.ddd.domain.event;

/**
 * @author Liangchen.Wang
 * Marker interface for a Domain Event.
 * 领域事件标记接口：具体领域事件必须继承AbstractDomainEvent（由框架ArchUnit规则domainEventsExtendBase守护），
 * 以统一获得eventId/occurredOn、值相等与不可变约束；本接口仅作类型标记。
 * 领域对象不可序列化直传，不继承Serializable：跨边界通信必须使用消息契约（发布语言）。
 */
public interface IDomainEvent {
}
