package wang.liangchen.matrix.shop.order.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.order.domain.order.Order;
import wang.liangchen.matrix.shop.order.domain.order.OrderId;
import wang.liangchen.matrix.shop.order.domain.order.UserId;
import wang.liangchen.matrix.shop.order.domain.readmodel.OrderDetail;
import wang.liangchen.matrix.shop.order.domain.readmodel.OrderSummary;

import java.util.List;
import java.util.Optional;

/**
 * 订单查询端口：CQRS查询侧，只读访问订单读模型。
 */
@Port(PortType.Repository)
public interface OrderQueryPort extends IRepositoryPort<OrderId, Order> {

    Optional<OrderDetail> queryById(OrderId orderId);

    List<OrderSummary> queryByBuyerId(UserId buyerId);
}
