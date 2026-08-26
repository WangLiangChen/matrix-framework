package wang.liangchen.matrix.shop.order.domain.readmodel;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.order.domain.order.Money;
import wang.liangchen.matrix.shop.order.domain.order.OrderId;
import wang.liangchen.matrix.shop.order.domain.order.OrderStatus;
import wang.liangchen.matrix.shop.order.domain.order.UserId;

/**
 * 订单摘要：订单列表读模型。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record OrderSummary(OrderId id, UserId buyerId, OrderStatus status, Money totalAmount) implements IValueObject {
}
