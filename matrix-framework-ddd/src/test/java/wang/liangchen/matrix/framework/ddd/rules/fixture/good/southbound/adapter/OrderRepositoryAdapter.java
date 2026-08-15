package wang.liangchen.matrix.framework.ddd.rules.fixture.good.southbound.adapter;

import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IRepositoryAdapter;

/** 合规仓储适配器 */
@Adapter(PortType.Repository)
public final class OrderRepositoryAdapter implements IRepositoryAdapter {
}
