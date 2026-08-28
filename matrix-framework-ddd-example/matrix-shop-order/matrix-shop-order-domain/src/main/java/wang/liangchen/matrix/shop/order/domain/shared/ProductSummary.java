package wang.liangchen.matrix.shop.order.domain.shared;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;

/**
 * 商品摘要：订单上下文对商品的理解（防腐层翻译后的订单领域值对象），
 * 仅保留下单与加购所需的最低价与商品名称，minPrice为SKU最低售价。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record ProductSummary(ProductId id, String productName, Money minPrice) implements IValueObject {
}
