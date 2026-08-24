package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.northbound.local;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ICommandRequest;

/** 违规：命令请求契约位于northbound.local包（放置规则：应在message包） */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.COMMAND_REQUEST, exchangePattern = MessageExchangePattern.FireAndForget)
public final class CommandInWrongPackage implements ICommandRequest {
}
