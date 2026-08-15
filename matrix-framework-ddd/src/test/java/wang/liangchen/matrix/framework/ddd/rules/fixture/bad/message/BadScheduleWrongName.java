package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.message;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ISchedulingRequest;

/** 违规：调度消息命名未以SchedulingRequest结尾 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.SCHEDULING, exchangePattern = MessageExchangePattern.FireAndForget)
public final class BadScheduleWrongName implements ISchedulingRequest {
}
