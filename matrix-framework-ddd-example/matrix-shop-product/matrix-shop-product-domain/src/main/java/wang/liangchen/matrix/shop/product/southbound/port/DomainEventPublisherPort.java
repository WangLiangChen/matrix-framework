package wang.liangchen.matrix.shop.product.southbound.port;

import wang.liangchen.matrix.framework.ddd.domain.event.IDomainEvent;
import wang.liangchen.matrix.framework.ddd.southbound.port.IPublisherPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

import java.util.List;

/**
 * 领域事件发布端口：隔离对事件总线的访问，实现位于南向适配层。
 * publish发布领域事件供本上下文订阅；publishContract发布经装配器翻译的事件契约（发布语言）供跨上下文订阅。
 * 端口位于领域层、不得依赖契约类型，故publishContract以Object承载；
 * 事件契约的类型约束由应用层的装配器翻译保证。
 */
@Port(PortType.Publisher)
public interface DomainEventPublisherPort extends IPublisherPort<IDomainEvent> {

    void publish(List<IDomainEvent> domainEvents);

    void publishContract(Object contractEvent);
}