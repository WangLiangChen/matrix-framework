package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/**
 * 订单已发货：订单完成发货的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class OrderShippedEvent extends AbstractDomainEvent {

    private final OrderId orderId;

    public OrderShippedEvent(OrderId orderId) {
        super();
        this.orderId = orderId;
    }

    public OrderId orderId() {
        return orderId;
    }
}