package wang.liangchen.matrix.shop.order.southbound.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.order.domain.cart.Cart;
import wang.liangchen.matrix.shop.order.domain.cart.CartId;
import wang.liangchen.matrix.shop.order.domain.order.UserId;
import wang.liangchen.matrix.shop.order.domain.shared.ProductId;

import java.util.List;
import java.util.Optional;

/**
 * 购物车仓储端口：以购物车聚合根为读写单位（findById/save/remove），实现位于南向适配层。
 */
@Port(PortType.Repository)
public interface CartRepositoryPort extends IRepositoryPort<CartId, Cart> {

    /**
     * 按买家查找购物车：事件订阅场景下（如订单已下单清空购物车）以买家定位聚合。
     */
    Optional<Cart> findByBuyerId(UserId buyerId);

    /**
     * 查找包含指定商品的所有购物车：事件订阅场景下（如商品已下架移除购物车商品）定位受影响聚合。
     */
    List<Cart> cartsContaining(ProductId productId);
}
