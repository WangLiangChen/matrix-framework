package wang.liangchen.matrix.shop.order.domain.cart;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.factory.IDomainFactory;
import wang.liangchen.matrix.shop.order.domain.order.UserId;
import wang.liangchen.matrix.shop.order.domain.readmodel.CartItemSummary;

import java.util.List;

/**
 * 购物车工厂：封装购物车的创建与重建逻辑。
 */
@DomainModel(DomainMetaModel.DomainFactory)
public final class CartFactory implements IDomainFactory {

    /**
     * 创建全新的购物车聚合（空购物车）。
     */
    public Cart create(CartId cartId, UserId buyerId) {
        return new Cart(cartId, buyerId, Cart.newItemList());
    }

    /**
     * 从持久化数据重建购物车聚合。
     */
    public Cart reconstitute(CartId id, UserId buyerId, List<CartItemSummary> itemSummaries) {
        List<CartItem> items = itemSummaries.stream()
                .map(summary -> CartItem.of(summary.productId(), summary.productName(), summary.unitPrice(), summary.quantity()))
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        return new Cart(id, buyerId, items);
    }
}
