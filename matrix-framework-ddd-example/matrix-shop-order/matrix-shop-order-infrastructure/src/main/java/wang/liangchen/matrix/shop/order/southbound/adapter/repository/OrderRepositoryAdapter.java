package wang.liangchen.matrix.shop.order.southbound.adapter.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.order.domain.order.*;
import wang.liangchen.matrix.shop.order.domain.port.OrderQueryPort;
import wang.liangchen.matrix.shop.order.domain.port.OrderRepositoryPort;
import wang.liangchen.matrix.shop.order.domain.readmodel.OrderDetail;
import wang.liangchen.matrix.shop.order.domain.readmodel.OrderItemSummary;
import wang.liangchen.matrix.shop.order.domain.readmodel.OrderSummary;

import java.util.List;
import java.util.Optional;

/**
 * 订单仓储适配器：实现订单仓储端口与订单查询端口，完成订单聚合与持久化对象之间的防腐翻译，
 * 重建聚合时委托订单工厂的reconstitute方法。
 */
@Repository
@Adapter(PortType.Repository)
public class OrderRepositoryAdapter implements OrderRepositoryPort, OrderQueryPort, IRepositoryAdapter {

    private final OrderDao orderDao;
    private final OrderFactory orderFactory = new OrderFactory();

    public OrderRepositoryAdapter(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(OrderId orderId) {
        return orderDao.findById(orderId.value()).map(this::reconstitute);
    }

    @Override
    public void save(Order order) {
        orderDao.save(toPo(order));
    }

    @Override
    public void remove(Order order) {
        orderDao.deleteById(order.id().value());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDetail> queryById(OrderId orderId) {
        return orderDao.findById(orderId.value()).map(this::orderDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummary> queryByBuyerId(UserId buyerId) {
        return orderDao.findByBuyerId(buyerId.value()).stream()
                .map(this::orderSummary)
                .toList();
    }

    private Order reconstitute(OrderPo po) {
        return orderFactory.reconstitute(
                OrderId.of(po.getId()), UserId.of(po.getBuyerId()),
                new Address(po.getReceiverName(), po.getReceiverPhone(), po.getReceiverProvince(),
                        po.getReceiverCity(), po.getReceiverDetail()),
                po.getItems().stream().map(this::orderItemSummary).toList(),
                OrderStatus.valueOf(po.getStatus()));
    }

    private OrderDetail orderDetail(OrderPo po) {
        return new OrderDetail(OrderId.of(po.getId()), UserId.of(po.getBuyerId()),
                new Address(po.getReceiverName(), po.getReceiverPhone(), po.getReceiverProvince(),
                        po.getReceiverCity(), po.getReceiverDetail()),
                po.getItems().stream().map(this::orderItemSummary).toList(),
                OrderStatus.valueOf(po.getStatus()), totalAmount(po));
    }

    private OrderSummary orderSummary(OrderPo po) {
        return new OrderSummary(OrderId.of(po.getId()), UserId.of(po.getBuyerId()),
                OrderStatus.valueOf(po.getStatus()), totalAmount(po));
    }

    private Money totalAmount(OrderPo po) {
        return po.getItems().stream()
                .map(item -> Money.of(item.getUnitPrice(), item.getCurrency()).multiply(item.getQuantity()))
                .reduce(Money.ZERO, Money::add);
    }

    private OrderPo toPo(Order order) {
        OrderPo po = new OrderPo();
        po.setId(order.id().value());
        po.setBuyerId(order.buyerId().value());
        po.setReceiverName(order.receiver().receiver());
        po.setReceiverPhone(order.receiver().phone());
        po.setReceiverProvince(order.receiver().province());
        po.setReceiverCity(order.receiver().city());
        po.setReceiverDetail(order.receiver().detail());
        po.setStatus(order.status().name());
        po.setItems(order.itemSummaries().stream().map(this::orderItemPo).toList());
        return po;
    }

    private OrderItemSummary orderItemSummary(OrderItemPo po) {
        return new OrderItemSummary(ProductId.of(po.getProductId()), po.getProductName(),
                Money.of(po.getUnitPrice(), po.getCurrency()), po.getQuantity());
    }

    private OrderItemPo orderItemPo(OrderItemSummary summary) {
        OrderItemPo po = new OrderItemPo();
        po.setProductId(summary.productId().value());
        po.setProductName(summary.productName());
        po.setUnitPrice(summary.unitPrice().amount());
        po.setCurrency(summary.unitPrice().currency());
        po.setQuantity(summary.quantity());
        return po;
    }
}
