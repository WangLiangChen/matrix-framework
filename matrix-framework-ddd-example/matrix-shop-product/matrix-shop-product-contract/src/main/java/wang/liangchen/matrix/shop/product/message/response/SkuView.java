package wang.liangchen.matrix.shop.product.message.response;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.response.IView;
import wang.liangchen.matrix.shop.product.message.request.AttributeValue;

import java.math.BigDecimal;
import java.util.List;


/**
 * SKU视图：商品明细中的SKU信息。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.VIEW, exchangePattern = MessageExchangePattern.RequestResponse)
public record SkuView(String id, List<AttributeValue> attributeValues, BigDecimal price, int stock) implements IView {
}
