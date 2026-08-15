package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.domain.port;

import wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.southbound.adapter.SomeAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.IPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

/** 违规：端口反向依赖适配器实现 */
@Port(PortType.Repository)
public interface PortDependsOnAdapter extends IPort {

    SomeAdapter adapter();
}
