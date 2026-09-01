package wang.liangchen.matrix.shop.order.southbound.adapter.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.AbstractRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.order.domain.order.*;
import wang.liangchen.matrix.shop.order.domain.port.OrderRepositoryPort;
import wang.liangchen.matrix.shop.order.domain.shared.Money;
import wang.liangchen.matrix.shop.order.domain.shared.ProductId;
import wang.liangchen.matrix.shop.order.domain.shared.TradeItemSummary;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 订单仓储适配器：实现订单仓储端口，完成订单聚合与持久化对象之间的防腐翻译，
 * 重建聚合时委托订单工厂的reconstitute方法；查询读侧经本端口承担，返回聚合根。
 */
@Repository
@Adapter(PortType.Repository)
public class OrderRepositoryAdapter extends AbstractRepositoryAdapter<OrderId, Order, OrderPo> implements OrderRepositoryPort {

    private final OrderDao orderDao;
    private final OrderFactory orderFactory = new OrderFactory();

    public OrderRepositoryAdapter(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    protected Optional<OrderPo> doFindById(OrderId id) {
        return orderDao.findById(id.value());
    }

    @Override
    protected void doSave(OrderPo po) {
        orderDao.save(po);
    }

    @Override
    protected void doRemoveById(OrderId id) {
        orderDao.deleteById(id.value());
    }

    @Override
    protected Order reconstitute(OrderPo po) {
        return orderFactory.reconstitute(
                OrderId.of(po.getId()), UserId.of(po.getBuyerId()),
                new Address(po.getReceiverName(), po.getReceiverPhone(), po.getReceiverProvince(),
                        po.getReceiverCity(), po.getReceiverDetail()),
                po.getItems().stream().map(this::orderItemSummary).toList(),
                Money.of(po.getTotalAmount(), po.getCurrency()),
                po.getPlacedOn(),
                OrderStatus.valueOf(po.getStatus()));
    }

    @Override
    protected OrderPo toPo(Order order) {
        OrderPo po = new OrderPo();
        po.setId(order.id().value());
        po.setBuyerId(order.buyerId().value());
        po.setReceiverName(order.receiver().receiver());
        po.setReceiverPhone(order.receiver().phone());
        po.setReceiverProvince(order.receiver().province());
        po.setReceiverCity(order.receiver().city());
        po.setReceiverDetail(order.receiver().detail());
        po.setStatus(order.status().name());
        po.setPlacedOn(order.placedOn());
        po.setTotalAmount(order.totalAmount().amount());
        po.setCurrency(order.totalAmount().currency());
        po.setItems(order.itemSummaries().stream().map(this::orderItemPo).toList());
        return po;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByBuyerId(UserId buyerId) {
        return orderDao.findByBuyerId(buyerId.value()).stream()
                .map(this::reconstitute)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findUnpaidCreatedBefore(Instant deadline) {
        return orderDao.findByStatusAndPlacedOnBefore(OrderStatus.Created.name(), deadline).stream()
                .map(this::reconstitute)
                .toList();
    }

    private TradeItemSummary orderItemSummary(OrderItemPo po) {
        return new TradeItemSummary(ProductId.of(po.getProductId()), po.getProductName(),
                Money.of(po.getUnitPrice(), po.getCurrency()), po.getQuantity());
    }

    private OrderItemPo orderItemPo(TradeItemSummary summary) {
        OrderItemPo po = new OrderItemPo();
        po.setProductId(summary.productId().value());
        po.setProductName(summary.productName());
        po.setUnitPrice(summary.unitPrice().amount());
        po.setCurrency(summary.unitPrice().currency());
        po.setQuantity(summary.quantity());
        return po;
    }
}