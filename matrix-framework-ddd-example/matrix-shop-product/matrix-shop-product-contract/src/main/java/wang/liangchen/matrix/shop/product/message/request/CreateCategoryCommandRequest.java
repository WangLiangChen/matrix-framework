package wang.liangchen.matrix.shop.product.message.request;
import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ICommandRequest;


/**
 * 创建类目命令请求：parentId为空表示创建顶级类目。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.COMMAND_REQUEST, exchangePattern = MessageExchangePattern.FireAndForget)
public record CreateCategoryCommandRequest(String name, String parentId) implements ICommandRequest {
}
