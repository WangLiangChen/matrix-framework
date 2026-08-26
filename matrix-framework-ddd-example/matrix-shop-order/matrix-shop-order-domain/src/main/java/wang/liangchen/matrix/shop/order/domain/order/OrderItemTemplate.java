package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;

/**
 * 订单项模板：下单时用于组装订单项的原始信息，商品名称与单价为下单时快照。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record OrderItemTemplate(ProductId productId, String productName, Money unitPrice, int quantity) implements IValueObject {
}
