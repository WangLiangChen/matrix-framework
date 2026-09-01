package wang.liangchen.matrix.shop.order.southbound.adapter.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.AbstractRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.order.domain.cart.Cart;
import wang.liangchen.matrix.shop.order.domain.cart.CartFactory;
import wang.liangchen.matrix.shop.order.domain.cart.CartId;
import wang.liangchen.matrix.shop.order.domain.order.UserId;
import wang.liangchen.matrix.shop.order.domain.port.CartRepositoryPort;
import wang.liangchen.matrix.shop.order.domain.shared.Money;
import wang.liangchen.matrix.shop.order.domain.shared.ProductId;
import wang.liangchen.matrix.shop.order.domain.shared.TradeItemSummary;

import java.util.List;
import java.util.Optional;

/**
 * 购物车仓储适配器：实现购物车仓储端口，完成购物车聚合与持久化对象之间的防腐翻译，
 * 重建聚合时委托购物车工厂的reconstitute方法。
 */
@Repository
@Adapter(PortType.Repository)
public class CartRepositoryAdapter extends AbstractRepositoryAdapter<CartId, Cart, CartPo> implements CartRepositoryPort {

    private final CartDao cartDao;
    private final CartFactory cartFactory = new CartFactory();

    public CartRepositoryAdapter(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    @Override
    protected Optional<CartPo> doFindById(CartId id) {
        return cartDao.findById(id.value());
    }

    @Override
    protected void doSave(CartPo po) {
        cartDao.save(po);
    }

    @Override
    protected void doRemoveById(CartId id) {
        cartDao.deleteById(id.value());
    }

    @Override
    protected Cart reconstitute(CartPo po) {
        return cartFactory.reconstitute(CartId.of(po.getId()), UserId.of(po.getBuyerId()),
                po.getItems().stream().map(this::cartItemSummary).toList());
    }

    @Override
    protected CartPo toPo(Cart cart) {
        CartPo po = new CartPo();
        po.setId(cart.id().value());
        po.setBuyerId(cart.buyerId().value());
        po.setItems(cart.itemSummaries().stream().map(this::cartItemPo).toList());
        return po;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> findByBuyerId(UserId buyerId) {
        return cartDao.findByBuyerId(buyerId.value()).map(this::reconstitute);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cart> cartsContaining(ProductId productId) {
        return cartDao.findByItemsProductId(productId.value()).stream().map(this::reconstitute).toList();
    }

    private TradeItemSummary cartItemSummary(CartItemPo po) {
        return new TradeItemSummary(ProductId.of(po.getProductId()), po.getProductName(),
                Money.of(po.getUnitPrice(), po.getCurrency()), po.getQuantity());
    }

    private CartItemPo cartItemPo(TradeItemSummary summary) {
        CartItemPo po = new CartItemPo();
        po.setProductId(summary.productId().value());
        po.setProductName(summary.productName());
        po.setUnitPrice(summary.unitPrice().amount());
        po.setCurrency(summary.unitPrice().currency());
        po.setQuantity(summary.quantity());
        return po;
    }
}