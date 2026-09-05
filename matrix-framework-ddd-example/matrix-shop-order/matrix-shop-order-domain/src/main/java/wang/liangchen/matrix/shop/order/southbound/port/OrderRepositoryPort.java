package wang.liangchen.matrix.shop.order.southbound.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.order.domain.order.Order;
import wang.liangchen.matrix.shop.order.domain.order.OrderId;
import wang.liangchen.matrix.shop.order.domain.order.UserId;

import java.time.Instant;
import java.util.List;

/**
 * 订单仓储端口：以订单聚合根为读写单位（findById/save/remove），实现位于南向适配层；
 * 查询读侧经本端口承担，统一语言命名的查询方法返回聚合根，供查询用例只读使用。
 */
@Port(PortType.Repository)
public interface OrderRepositoryPort extends IRepositoryPort<OrderId, Order> {

    /**
     * 查询买家订单：按买家标识返回订单聚合根列表。
     */
    List<Order> findByBuyerId(UserId buyerId);

    /**
     * 查询超时未支付订单：返回指定时刻之前下单且仍处于待支付状态的订单聚合根列表，
     * 供调度场景（超时未支付自动取消）读侧使用。
     */
    List<Order> findUnpaidCreatedBefore(Instant deadline);
}
