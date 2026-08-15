package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IClientPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

/** 违规：@Port类型与所实现的端口标记接口不匹配 */
@Port(PortType.Repository)
public interface BadPortWrongType extends IClientPort {
}
