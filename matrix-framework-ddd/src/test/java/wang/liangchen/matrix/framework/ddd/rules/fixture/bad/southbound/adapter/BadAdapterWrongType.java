package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.southbound.adapter;

import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

/** 违规：实现IRepositoryAdapter但标注@Adapter(Client) */
@Adapter(PortType.Client)
public final class BadAdapterWrongType implements IRepositoryAdapter {
}
