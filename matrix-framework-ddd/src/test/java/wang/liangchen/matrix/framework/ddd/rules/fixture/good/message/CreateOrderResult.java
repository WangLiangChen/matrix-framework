package wang.liangchen.matrix.framework.ddd.rules.fixture.good.message;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.response.IResult;

/** 合规命令响应契约 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.RESULT, exchangePattern = MessageExchangePattern.FireAndForget)
public final class CreateOrderResult implements IResult {

    private final String orderId;

    public CreateOrderResult(String orderId) {
        this.orderId = orderId;
    }
}
