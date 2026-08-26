package wang.liangchen.matrix.shop.product.message.response;
import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.response.IResult;


/**
 * 创建类目命令响应。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.RESULT, exchangePattern = MessageExchangePattern.FireAndForget)
public record CreateCategoryResult(String categoryId) implements IResult {
}
