package wang.liangchen.matrix.shop.order.domain.readmodel;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.order.domain.order.*;

import java.util.List;

/**
 * 订单明细：订单详情读模型。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record OrderDetail(OrderId id, UserId buyerId, Address receiver,
                          List<OrderItemSummary> items, OrderStatus status, Money totalAmount) implements IValueObject {
}
