package wang.liangchen.matrix.shop.order.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.order.domain.cart.Cart;
import wang.liangchen.matrix.shop.order.domain.cart.CartId;

/**
 * 购物车仓储端口：以购物车聚合根为读写单位（findById/save/remove），实现位于南向适配层。
 */
@Port(PortType.Repository)
public interface CartRepositoryPort extends IRepositoryPort<CartId, Cart> {
}
