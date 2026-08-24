package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.southbound;

import wang.liangchen.matrix.framework.ddd.southbound.port.IPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

/** 违规：业务端口位于southbound根包（放置规则：应在domain.port） */
@Port(PortType.Repository)
public interface PortInWrongPackage extends IPort {
}
