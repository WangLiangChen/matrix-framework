package wang.liangchen.matrix.shop.product.message.request;
import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.IRequest;


/**
 * 属性值：消息契约中商品（或SKU）对某个属性的取值。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.REQUEST, exchangePattern = MessageExchangePattern.RequestResponse)
public record AttributeValue(String attributeId, String value) implements IRequest {
}
