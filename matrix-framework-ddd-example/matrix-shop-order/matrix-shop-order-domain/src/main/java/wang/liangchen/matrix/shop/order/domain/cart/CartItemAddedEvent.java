package wang.liangchen.matrix.shop.order.domain.cart;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;
import wang.liangchen.matrix.shop.order.domain.shared.ProductId;

/**
 * 商品已加入购物车：购物车发生商品加入的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class CartItemAddedEvent extends AbstractDomainEvent {

    private final CartId cartId;
    private final ProductId productId;
    private final int quantity;

    public CartItemAddedEvent(CartId cartId, ProductId productId, int quantity) {
        super();
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public CartId cartId() {
        return cartId;
    }

    public ProductId productId() {
        return productId;
    }

    public int quantity() {
        return quantity;
    }
}