package wang.liangchen.matrix.shop.order.northbound.assembler;

import wang.liangchen.matrix.framework.ddd.assembler.AbstractAssembler;
import wang.liangchen.matrix.framework.ddd.assembler.Assembler;
import wang.liangchen.matrix.shop.order.domain.order.Address;
import wang.liangchen.matrix.shop.order.domain.order.Order;
import wang.liangchen.matrix.shop.order.domain.order.OrderItemTemplate;
import wang.liangchen.matrix.shop.order.domain.shared.ProductSummary;
import wang.liangchen.matrix.shop.order.domain.shared.TradeItemSummary;
import wang.liangchen.matrix.shop.order.message.request.PlaceOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.response.OrderDetailView;
import wang.liangchen.matrix.shop.order.message.response.OrderItemView;
import wang.liangchen.matrix.shop.order.message.response.OrderView;

/**
 * 订单装配器：消息契约与订单领域对象之间的双向转换——
 * 入站将下单命令请求装配为领域值对象（地址、订单项模板，兼任工厂），
 * 出站将订单聚合装配为查询视图，只做字段映射与类型转换，不含业务规则。
 */
@Assembler
public class OrderAssembler extends AbstractAssembler {

    /**
     * 入站：下单请求的收货人契约 → 地址值对象。
     */
    public Address toAddress(PlaceOrderCommandRequest.Receiver receiver) {
        return new Address(receiver.receiver(), receiver.phone(), receiver.province(), receiver.city(), receiver.detail());
    }

    /**
     * 入站：商品快照（防腐层获取）+ 数量 → 订单项模板值对象。
     */
    public OrderItemTemplate toOrderItemTemplate(ProductSummary summary, int quantity) {
        return new OrderItemTemplate(summary.id(), summary.productName(), summary.minPrice(), quantity);
    }

    /**
     * 出站：订单聚合 → 订单视图。
     */
    public OrderView toOrderView(Order order) {
        return new OrderView(order.id().value(), order.buyerId().value(),
                order.status().name(), order.totalAmount().amount());
    }

    /**
     * 出站：订单聚合 → 订单明细视图。
     */
    public OrderDetailView toOrderDetailView(Order order) {
        return new OrderDetailView(order.id().value(), order.buyerId().value(), toReceiver(order.receiver()),
                order.itemSummaries().stream().map(this::toOrderItemView).toList(),
                order.status().name(), order.totalAmount().amount());
    }

    /**
     * 出站：订单项摘要 → 订单项视图。
     */
    public OrderItemView toOrderItemView(TradeItemSummary item) {
        return new OrderItemView(item.productId().value(), item.productName(),
                item.unitPrice().amount(), item.quantity(), item.unitPrice().multiply(item.quantity()).amount());
    }

    /**
     * 出站：地址值对象 → 收货人视图。
     */
    public PlaceOrderCommandRequest.Receiver toReceiver(Address address) {
        return new PlaceOrderCommandRequest.Receiver(address.receiver(), address.phone(),
                address.province(), address.city(), address.detail());
    }
}
