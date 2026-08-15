package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.message;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ICommandRequest;
import wang.liangchen.matrix.framework.ddd.domain.identity.IIdentity;

/** 违规：消息契约引用框架领域类型（发布语言与领域模型隔离，身份标识以基本类型值承载） */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.COMMAND_REQUEST, exchangePattern = MessageExchangePattern.FireAndForget)
public final class BadContractRefersFrameworkDomain implements ICommandRequest {

    private final IIdentity id;

    public BadContractRefersFrameworkDomain(IIdentity id) {
        this.id = id;
    }
}
