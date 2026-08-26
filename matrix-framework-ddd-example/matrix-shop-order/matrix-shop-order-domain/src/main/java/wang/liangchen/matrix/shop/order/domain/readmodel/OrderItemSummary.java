package wang.liangchen.matrix.shop.order.domain.readmodel;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.order.domain.order.Money;
import wang.liangchen.matrix.shop.order.domain.order.ProductId;

/**
 * 订单项摘要：订单项读模型，同时作为订单工厂重建订单项的快照。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record OrderItemSummary(ProductId productId, String productName, Money unitPrice, int quantity) implements IValueObject {
}
