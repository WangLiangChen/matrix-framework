package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AbstractAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.domain.readmodel.OrderItemSummary;

import java.util.List;

/**
 * 订单聚合根：表达买家的下单交易，聚合内部包含订单项实体，
 * 维护订单状态机（待支付→已支付→已发货→已完成，待支付→已取消），
 * 通过商品标识(ProductId)引用商品聚合，商品名称与单价为下单时快照。
 */
@DomainModel(DomainMetaModel.AggregateRoot)
public final class Order extends AbstractAggregateRoot<OrderId> implements IAggregateRoot<OrderId> {

    @Identity
    private final OrderId id;
    private final UserId buyerId;
    private final Address receiver;
    private final List<OrderItem> items;
    private OrderStatus status;

    Order(OrderId id, UserId buyerId, Address receiver, List<OrderItem> items, OrderStatus status) {
        this.id = id;
        this.buyerId = buyerId;
        this.receiver = receiver;
        this.items = items;
        this.status = status;
    }

    public OrderId id() {
        return id;
    }

    public UserId buyerId() {
        return buyerId;
    }

    public Address receiver() {
        return receiver;
    }

    public OrderStatus status() {
        return status;
    }

    public List<OrderItemSummary> itemSummaries() {
        return items.stream()
                .map(item -> new OrderItemSummary(item.productId(), item.productName(), item.unitPrice(), item.quantity()))
                .toList();
    }

    /**
     * 订单总额：各订单项小计之和。
     */
    public Money totalAmount() {
        return items.stream()
                .map(OrderItem::subtotal)
                .reduce(Money.ZERO, Money::add);
    }

    /**
     * 收集下单领域事实：仅由订单工厂在创建新订单时调用
     * （AbstractAggregateRoot#raise为受保护成员，事件由聚合自身收集）。
     */
    void placed() {
        raise(new OrderPlaced(id, buyerId, totalAmount()));
    }

    /**
     * 支付订单：待支付→已支付。
     */
    public void pay() {
        requireStatus(OrderStatus.Created, "支付");
        this.status = OrderStatus.Paid;
        raise(new OrderPaid(id));
    }

    /**
     * 发货：已支付→已发货。
     */
    public void ship() {
        requireStatus(OrderStatus.Paid, "发货");
        this.status = OrderStatus.Shipped;
        raise(new OrderShipped(id));
    }

    /**
     * 完成订单：已发货→已完成。
     */
    public void complete() {
        requireStatus(OrderStatus.Shipped, "完成");
        this.status = OrderStatus.Completed;
        raise(new OrderCompleted(id));
    }

    /**
     * 取消订单：待支付→已取消。
     */
    public void cancel() {
        requireStatus(OrderStatus.Created, "取消");
        this.status = OrderStatus.Canceled;
        raise(new OrderCanceled(id));
    }

    private void requireStatus(OrderStatus expected, String action) {
        if (this.status != expected) {
            throw new DomainException("订单状态不允许" + action);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Order that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
