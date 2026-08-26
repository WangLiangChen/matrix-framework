package wang.liangchen.matrix.shop.product.message.request;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ICommandRequest;

import java.util.List;


/**
 * 创建属性命令请求：type取值General(一般属性)/Key(关键属性)/Sales(销售属性)。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.COMMAND_REQUEST, exchangePattern = MessageExchangePattern.FireAndForget)
public record CreateAttributeCommandRequest(String name, String type, List<String> options) implements ICommandRequest {
}
