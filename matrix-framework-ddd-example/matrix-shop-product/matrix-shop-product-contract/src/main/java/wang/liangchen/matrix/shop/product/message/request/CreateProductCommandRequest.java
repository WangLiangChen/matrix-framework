package wang.liangchen.matrix.shop.product.message.request;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ICommandRequest;
import wang.liangchen.matrix.framework.ddd.contract.request.IRequest;

import java.math.BigDecimal;
import java.util.List;


/**
 * 创建商品命令请求：创建SPU及其SKU。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.COMMAND_REQUEST, exchangePattern = MessageExchangePattern.FireAndForget)
public record CreateProductCommandRequest(String name, String subtitle, String categoryId, String brandId,
                                          List<AttributeValue> attributeValues, List<Sku> skus) implements ICommandRequest {

    /**
     * SKU：命令请求中携带的SKU原始信息。
     */
    @MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.REQUEST, exchangePattern = MessageExchangePattern.RequestResponse)
    public record Sku(List<AttributeValue> attributeValues, BigDecimal price, int stock) implements IRequest {
    }
}
