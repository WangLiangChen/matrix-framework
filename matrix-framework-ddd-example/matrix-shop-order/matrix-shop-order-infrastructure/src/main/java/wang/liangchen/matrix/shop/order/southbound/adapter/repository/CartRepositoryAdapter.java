package wang.liangchen.matrix.shop.order.southbound.adapter.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.order.domain.cart.Cart;
import wang.liangchen.matrix.shop.order.domain.cart.CartFactory;
import wang.liangchen.matrix.shop.order.domain.cart.CartId;
import wang.liangchen.matrix.shop.order.domain.order.Money;
import wang.liangchen.matrix.shop.order.domain.order.ProductId;
import wang.liangchen.matrix.shop.order.domain.order.UserId;
import wang.liangchen.matrix.shop.order.domain.port.CartQueryPort;
import wang.liangchen.matrix.shop.order.domain.port.CartRepositoryPort;
import wang.liangchen.matrix.shop.order.domain.readmodel.CartDetail;
import wang.liangchen.matrix.shop.order.domain.readmodel.CartItemSummary;

import java.util.Optional;

/**
 * 购物车仓储适配器：实现购物车仓储端口与购物车查询端口，完成购物车聚合与持久化对象之间的防腐翻译，
 * 重建聚合时委托购物车工厂的reconstitute方法。
 */
@Repository
@Adapter(PortType.Repository)
public class CartRepositoryAdapter implements CartRepositoryPort, CartQueryPort, IRepositoryAdapter {

    private final CartDao cartDao;
    private final CartFactory cartFactory = new CartFactory();

    public CartRepositoryAdapter(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> findById(CartId cartId) {
        return cartDao.findById(cartId.value()).map(this::reconstitute);
    }

    @Override
    public void save(Cart cart) {
        cartDao.save(toPo(cart));
    }

    @Override
    public void remove(Cart cart) {
        cartDao.deleteById(cart.id().value());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CartDetail> queryById(CartId cartId) {
        return cartDao.findById(cartId.value()).map(this::cartDetail);
    }

    private Cart reconstitute(CartPo po) {
        return cartFactory.reconstitute(CartId.of(po.getId()), UserId.of(po.getBuyerId()),
                po.getItems().stream().map(this::cartItemSummary).toList());
    }

    private CartDetail cartDetail(CartPo po) {
        return new CartDetail(CartId.of(po.getId()), UserId.of(po.getBuyerId()),
                po.getItems().stream().map(this::cartItemSummary).toList(),
                po.getItems().stream()
                        .map(item -> Money.of(item.getUnitPrice(), item.getCurrency()).multiply(item.getQuantity()))
                        .reduce(Money.ZERO, Money::add));
    }

    private CartPo toPo(Cart cart) {
        CartPo po = new CartPo();
        po.setId(cart.id().value());
        po.setBuyerId(cart.buyerId().value());
        po.setItems(cart.itemSummaries().stream().map(this::cartItemPo).toList());
        return po;
    }

    private CartItemSummary cartItemSummary(CartItemPo po) {
        return new CartItemSummary(ProductId.of(po.getProductId()), po.getProductName(),
                Money.of(po.getUnitPrice(), po.getCurrency()), po.getQuantity());
    }

    private CartItemPo cartItemPo(CartItemSummary summary) {
        CartItemPo po = new CartItemPo();
        po.setProductId(summary.productId().value());
        po.setProductName(summary.productName());
        po.setUnitPrice(summary.unitPrice().amount());
        po.setCurrency(summary.unitPrice().currency());
        po.setQuantity(summary.quantity());
        return po;
    }
}
