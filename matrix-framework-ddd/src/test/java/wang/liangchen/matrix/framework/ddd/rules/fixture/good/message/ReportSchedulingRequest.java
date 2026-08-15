package wang.liangchen.matrix.framework.ddd.rules.fixture.good.message;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ISchedulingRequest;

/** 合规调度消息契约 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.SCHEDULING, exchangePattern = MessageExchangePattern.FireAndForget)
public final class ReportSchedulingRequest implements ISchedulingRequest {
}
