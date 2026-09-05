package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.factory.AbstractDomainFactory;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.domain.shared.Money;
import wang.liangchen.matrix.shop.order.domain.shared.TradeItemSummary;

import java.time.Instant;
import java.util.List;

/**
 * 订单工厂：封装订单与订单项的创建与重建逻辑。
 * 创建全新订单使用{@link #create}（经订单定价领域服务计算总额），
 * 从持久化数据重建聚合使用{@link #reconstitute}。
 */
@DomainModel(DomainMetaModel.DomainFactory)
public final class OrderFactory extends AbstractDomainFactory {

    /**
     * 创建全新的订单聚合，下单时商品名称与单价已快照在订单项规格中，
     * 订单总额经订单定价领域服务按大宗与忠诚折扣计算。
     * 订单定价领域服务由调用方显式传入，使依赖关系显式化。
     */
    public Order create(UserId buyerId, Address receiver, List<OrderItemSpec> itemSpecs, LoyaltyLevel loyalty, OrderPricingService pricingService) {
        if (itemSpecs == null || itemSpecs.isEmpty()) {
            throw new DomainException("订单必须包含商品");
        }
        List<OrderItem> items = itemSpecs.stream()
                .map(spec -> OrderItem.of(spec.productId(), spec.productName(), spec.unitPrice(), spec.quantity()))
                .toList();
        Money totalAmount = pricingService.totalOf(itemSpecs, loyalty);
        Order order = new Order(OrderId.generate(), buyerId, receiver, items, totalAmount, Instant.now(), OrderStatus.Created);
        order.placed();
        return order;
    }

    /**
     * 从持久化数据重建订单聚合。
     */
    public Order reconstitute(OrderId id, UserId buyerId, Address receiver,
                              List<TradeItemSummary> itemSummaries, Money totalAmount, Instant placedOn, OrderStatus status) {
        List<OrderItem> items = itemSummaries.stream()
                .map(summary -> OrderItem.of(summary.productId(), summary.productName(), summary.unitPrice(), summary.quantity()))
                .toList();
        return new Order(id, buyerId, receiver, items, totalAmount, placedOn, status);
    }
}