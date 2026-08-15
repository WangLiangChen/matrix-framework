package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.message;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ICommandRequest;

/** 违规：命令请求的exchangePattern应为FireAndForget而非RequestResponse */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.COMMAND_REQUEST, exchangePattern = MessageExchangePattern.RequestResponse)
public final class BadCommandWrongExchangePattern implements ICommandRequest {
}
