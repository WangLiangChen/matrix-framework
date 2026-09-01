package wang.liangchen.matrix.shop.order.domain.port;

import wang.liangchen.matrix.framework.ddd.domain.event.IDomainEvent;
import wang.liangchen.matrix.framework.ddd.southbound.port.IPublisherPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

import java.util.List;

/**
 * 领域事件发布端口：隔离对事件总线的访问，实现位于南向适配层。
 */
@Port(PortType.Publisher)
public interface DomainEventPublisherPort extends IPublisherPort<IDomainEvent> {

    void publish(List<IDomainEvent> domainEvents);
}