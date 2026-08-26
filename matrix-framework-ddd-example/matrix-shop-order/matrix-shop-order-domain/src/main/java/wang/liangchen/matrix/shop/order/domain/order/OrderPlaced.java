package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/**
 * 订单已下单：订单创建成功的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class OrderPlaced extends AbstractDomainEvent {

    private final OrderId orderId;
    private final UserId buyerId;
    private final Money totalAmount;

    public OrderPlaced(OrderId orderId, UserId buyerId, Money totalAmount) {
        super();
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.totalAmount = totalAmount;
    }

    public OrderId orderId() {
        return orderId;
    }

    public UserId buyerId() {
        return buyerId;
    }

    public Money totalAmount() {
        return totalAmount;
    }
}
