package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/**
 * 订单已支付：订单完成支付的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class OrderPaid extends AbstractDomainEvent {

    private final OrderId orderId;

    public OrderPaid(OrderId orderId) {
        super();
        this.orderId = orderId;
    }

    public OrderId orderId() {
        return orderId;
    }
}
