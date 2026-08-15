package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.message;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ICommandRequest;

/** 违规：命令请求命名未以CommandRequest结尾 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.COMMAND_REQUEST, exchangePattern = MessageExchangePattern.FireAndForget)
public final class BadCommandWrongName implements ICommandRequest {
}
