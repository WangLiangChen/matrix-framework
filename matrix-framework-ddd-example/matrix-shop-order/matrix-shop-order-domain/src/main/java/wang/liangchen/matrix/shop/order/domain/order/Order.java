package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AbstractAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.domain.shared.Money;
import wang.liangchen.matrix.shop.order.domain.shared.TradeItemSummary;

import java.time.Instant;
import java.util.List;

/**
 * 订单聚合根：表达买家的下单交易，聚合内部包含订单项实体，
 * 维护订单状态机（待支付→已支付→已发货→已完成，待支付→已取消），
 * 通过商品标识(ProductId)引用商品聚合，商品名称与单价为下单时快照。
 */
@DomainModel(DomainMetaModel.AggregateRoot)
public final class Order extends AbstractAggregateRoot<OrderId> {

    @Identity
    private final OrderId id;
    private final UserId buyerId;
    private final Address receiver;
    private final List<OrderItem> items;
    private final Money totalAmount;
    private final Instant placedOn;
    private OrderStatus status;

    Order(OrderId id, UserId buyerId, Address receiver, List<OrderItem> items, Money totalAmount, Instant placedOn, OrderStatus status) {
        this.id = id;
        this.buyerId = buyerId;
        this.receiver = receiver;
        this.items = items;
        this.totalAmount = totalAmount;
        this.placedOn = placedOn;
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

    public List<TradeItemSummary> itemSummaries() {
        return items.stream()
                .map(item -> new TradeItemSummary(item.productId(), item.productName(), item.unitPrice(), item.quantity()))
                .toList();
    }

    /**
     * 订单总额：下单时由订单定价领域服务计算（含大宗与忠诚折扣），此后不变。
     */
    public Money totalAmount() {
        return totalAmount;
    }

    /**
     * 下单时间：调度场景判断订单是否超时未支付的依据，下单后不变。
     */
    public Instant placedOn() {
        return placedOn;
    }

    /**
     * 收集下单领域事实：仅由订单工厂在创建新订单时调用
     * （AbstractAggregateRoot#raise为受保护成员，事件由聚合自身收集）。
     */
    void placed() {
        raise(new OrderPlacedEvent(id, buyerId, totalAmount));
    }

    /**
     * 支付订单：待支付→已支付。
     */
    public void pay() {
        requireStatus(OrderStatus.Created, "支付");
        this.status = OrderStatus.Paid;
        raise(new OrderPaidEvent(id));
    }

    /**
     * 发货：已支付→已发货。
     */
    public void ship() {
        requireStatus(OrderStatus.Paid, "发货");
        this.status = OrderStatus.Shipped;
        raise(new OrderShippedEvent(id));
    }

    /**
     * 完成订单：已发货→已完成。
     */
    public void complete() {
        requireStatus(OrderStatus.Shipped, "完成");
        this.status = OrderStatus.Completed;
        raise(new OrderCompletedEvent(id));
    }

    /**
     * 取消订单：待支付→已取消。
     */
    public void cancel() {
        requireStatus(OrderStatus.Created, "取消");
        this.status = OrderStatus.Canceled;
        raise(new OrderCanceledEvent(id));
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