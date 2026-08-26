package wang.liangchen.matrix.shop.order.domain.readmodel;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.order.domain.order.Money;
import wang.liangchen.matrix.shop.order.domain.order.ProductId;

/**
 * 购物车项摘要：购物车项读模型，同时作为购物车工厂重建购物车项的快照。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record CartItemSummary(ProductId productId, String productName, Money unitPrice, int quantity) implements IValueObject {
}
