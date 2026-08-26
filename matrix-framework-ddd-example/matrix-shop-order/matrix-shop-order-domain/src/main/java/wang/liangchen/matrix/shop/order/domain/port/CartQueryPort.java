package wang.liangchen.matrix.shop.order.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.order.domain.cart.Cart;
import wang.liangchen.matrix.shop.order.domain.cart.CartId;
import wang.liangchen.matrix.shop.order.domain.readmodel.CartDetail;

import java.util.Optional;

/**
 * 购物车查询端口：CQRS查询侧，只读访问购物车读模型。
 */
@Port(PortType.Repository)
public interface CartQueryPort extends IRepositoryPort<CartId, Cart> {

    Optional<CartDetail> queryById(CartId cartId);
}
