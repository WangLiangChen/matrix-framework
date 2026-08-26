package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/**
 * 订单已取消：订单被取消的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class OrderCanceled extends AbstractDomainEvent {

    private final OrderId orderId;

    public OrderCanceled(OrderId orderId) {
        super();
        this.orderId = orderId;
    }

    public OrderId orderId() {
        return orderId;
    }
}
