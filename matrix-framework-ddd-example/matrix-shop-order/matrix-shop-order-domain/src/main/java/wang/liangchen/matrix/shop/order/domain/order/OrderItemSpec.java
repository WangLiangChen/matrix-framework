package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.order.domain.shared.Money;
import wang.liangchen.matrix.shop.order.domain.shared.ProductId;

/**
 * 订单项规格：下单时用于组装订单项的原始信息，商品名称与单价为下单时快照。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record OrderItemSpec(ProductId productId, String productName, Money unitPrice, int quantity) implements IValueObject {
}