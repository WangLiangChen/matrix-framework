package wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.port;

import wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.order.Order;
import wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.order.OrderId;
import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

/** 合规仓储端口 */
@Port(PortType.Repository)
public interface OrderRepositoryPort extends IRepositoryPort<OrderId, Order> {
}
