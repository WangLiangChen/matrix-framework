package wang.liangchen.matrix.framework.ddd.rules.fixture.good.message;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ICommandRequest;

/** 合规命令请求契约 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.COMMAND_REQUEST, exchangePattern = MessageExchangePattern.FireAndForget)
public final class CreateOrderCommandRequest implements ICommandRequest {

    private final String customer;

    public CreateOrderCommandRequest(String customer) {
        this.customer = customer;
    }
}
