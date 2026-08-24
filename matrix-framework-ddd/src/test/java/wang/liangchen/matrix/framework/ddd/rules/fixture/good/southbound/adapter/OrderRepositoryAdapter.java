package wang.liangchen.matrix.framework.ddd.rules.fixture.good.southbound.adapter;

import wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.order.Order;
import wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.order.OrderId;
import wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.port.OrderRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

import java.util.Optional;

/** 合规仓储适配器：实现业务端口（依赖倒置装配完整）并标注框架适配器标记接口 */
@Adapter(PortType.Repository)
public final class OrderRepositoryAdapter implements IRepositoryAdapter, OrderRepositoryPort {

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.empty();
    }

    @Override
    public void save(Order aggregateRoot) {
    }

    @Override
    public void remove(Order aggregateRoot) {
    }
}
