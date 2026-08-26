package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.factory.IDomainFactory;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.domain.readmodel.OrderItemSummary;

import java.util.List;

/**
 * 订单工厂：封装订单与订单项的创建与重建逻辑。
 * 创建全新订单使用{@link #create}，从持久化数据重建聚合使用{@link #reconstitute}。
 */
@DomainModel(DomainMetaModel.DomainFactory)
public final class OrderFactory implements IDomainFactory {

    /**
     * 创建全新的订单聚合，下单时商品名称与单价已快照在订单项模板中。
     */
    public Order create(UserId buyerId, Address receiver, List<OrderItemTemplate> itemTemplates) {
        if (itemTemplates == null || itemTemplates.isEmpty()) {
            throw new DomainException("订单必须包含商品");
        }
        List<OrderItem> items = itemTemplates.stream()
                .map(template -> OrderItem.of(template.productId(), template.productName(), template.unitPrice(), template.quantity()))
                .toList();
        Order order = new Order(OrderId.generate(), buyerId, receiver, items, OrderStatus.Created);
        order.placed();
        return order;
    }

    /**
     * 从持久化数据重建订单聚合。
     */
    public Order reconstitute(OrderId id, UserId buyerId, Address receiver,
                              List<OrderItemSummary> itemSummaries, OrderStatus status) {
        List<OrderItem> items = itemSummaries.stream()
                .map(summary -> OrderItem.of(summary.productId(), summary.productName(), summary.unitPrice(), summary.quantity()))
                .toList();
        return new Order(id, buyerId, receiver, items, status);
    }
}
