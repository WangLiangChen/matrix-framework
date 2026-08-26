package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/**
 * 订单已完成：订单交易完成的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class OrderCompleted extends AbstractDomainEvent {

    private final OrderId orderId;

    public OrderCompleted(OrderId orderId) {
        super();
        this.orderId = orderId;
    }

    public OrderId orderId() {
        return orderId;
    }
}
