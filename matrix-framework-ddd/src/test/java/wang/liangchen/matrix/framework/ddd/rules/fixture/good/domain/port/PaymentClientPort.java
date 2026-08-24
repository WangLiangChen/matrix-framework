package wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IClientPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

/** 合规客户端端口：隔离对上游限界上下文的访问（防腐层的端口侧，南向依赖的依赖倒置） */
@Port(PortType.Client)
public interface PaymentClientPort extends IClientPort {
}
