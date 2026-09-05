package wang.liangchen.matrix.shop.order.northbound.assembler;

import wang.liangchen.matrix.framework.ddd.assembler.AbstractAssembler;
import wang.liangchen.matrix.framework.ddd.assembler.Assembler;
import wang.liangchen.matrix.shop.order.domain.cart.Cart;
import wang.liangchen.matrix.shop.order.domain.shared.TradeItemSummary;
import wang.liangchen.matrix.shop.order.message.response.CartItemView;
import wang.liangchen.matrix.shop.order.message.response.CartView;

/**
 * 购物车装配器：出站装配——将购物车聚合装配为查询视图，
 * 只做字段映射与类型转换，不含业务规则。
 */
@Assembler
public class CartAssembler extends AbstractAssembler {

    /**
     * 出站：购物车聚合 → 购物车视图。
     */
    public CartView toCartView(Cart cart) {
        return new CartView(cart.id().value(), cart.buyerId().value(),
                cart.itemSummaries().stream().map(this::toCartItemView).toList(),
                cart.totalAmount().amount());
    }

    /**
     * 出站：交易项摘要 → 购物车项视图。
     */
    public CartItemView toCartItemView(TradeItemSummary item) {
        return new CartItemView(item.productId().value(), item.productName(),
                item.unitPrice().amount(), item.quantity(), item.unitPrice().multiply(item.quantity()).amount());
    }
}