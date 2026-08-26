package wang.liangchen.matrix.shop.product.message.response;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.response.IView;
import wang.liangchen.matrix.shop.product.message.request.AttributeValue;

import java.util.List;


/**
 * 商品明细视图：商品详情查询视图，也是向订单等下游上下文开放的发布语言。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.VIEW, exchangePattern = MessageExchangePattern.RequestResponse)
public record ProductDetailView(String id, String name, String subtitle,
                                String categoryId, String brandId, boolean listed,
                                List<AttributeValue> attributeValues, List<SkuView> skus) implements IView {
}
