package wang.liangchen.matrix.shop.order.domain.shared;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;

/**
 * 交易项摘要：订单上下文中商品交易行的值对象快照，
 * 统一表达购物车项与订单项（同一商品以某单价买某数量），
 * 同时作为购物车工厂与订单工厂重建聚合内部实体的入参。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record TradeItemSummary(ProductId productId, String productName, Money unitPrice, int quantity) implements IValueObject {
}
