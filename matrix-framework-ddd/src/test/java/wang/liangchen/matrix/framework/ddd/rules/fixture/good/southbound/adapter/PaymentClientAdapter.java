package wang.liangchen.matrix.framework.ddd.rules.fixture.good.southbound.adapter;

import wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.port.PaymentClientPort;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IClientAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;

/** 合规客户端适配器：实现业务客户端端口，承担跨上下文防腐层职责（翻译上游模型） */
@Adapter(PortType.Client)
public final class PaymentClientAdapter implements IClientAdapter, PaymentClientPort {
}
