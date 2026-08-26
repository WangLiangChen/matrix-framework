package wang.liangchen.matrix.shop.order.domain.readmodel;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.order.domain.cart.CartId;
import wang.liangchen.matrix.shop.order.domain.order.Money;
import wang.liangchen.matrix.shop.order.domain.order.UserId;

import java.util.List;

/**
 * 购物车明细：购物车详情读模型。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record CartDetail(CartId id, UserId buyerId, List<CartItemSummary> items, Money totalAmount) implements IValueObject {
}
