package wang.liangchen.matrix.shop.order.domain.cart;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AbstractAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.domain.order.UserId;
import wang.liangchen.matrix.shop.order.domain.shared.Money;
import wang.liangchen.matrix.shop.order.domain.shared.ProductId;
import wang.liangchen.matrix.shop.order.domain.shared.TradeItemSummary;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车聚合根：表达买家的待购商品集合，
 * 维护不变式：同一商品在购物车中只存在一个购物车项，商品数量必须大于零。
 */
@DomainModel(DomainMetaModel.AggregateRoot)
public final class Cart extends AbstractAggregateRoot<CartId> implements IAggregateRoot<CartId> {

    @Identity
    private final CartId id;
    private final UserId buyerId;
    private final List<CartItem> items;

    Cart(CartId id, UserId buyerId, List<CartItem> items) {
        this.id = id;
        this.buyerId = buyerId;
        this.items = items;
    }

    public CartId id() {
        return id;
    }

    public UserId buyerId() {
        return buyerId;
    }

    public List<TradeItemSummary> itemSummaries() {
        return items.stream()
                .map(item -> new TradeItemSummary(item.productId(), item.productName(), item.unitPrice(), item.quantity()))
                .toList();
    }

    /**
     * 购物车总额：各购物车项小计之和。
     */
    public Money totalAmount() {
        return items.stream()
                .map(CartItem::subtotal)
                .reduce(Money.ZERO, Money::add);
    }

    /**
     * 加入商品：同一商品已存在时合并数量，否则新增购物车项。
     */
    public void addItem(ProductId productId, String productName, Money unitPrice, int quantity) {
        CartItem existing = itemOf(productId);
        if (existing == null) {
            items.add(CartItem.of(productId, productName, unitPrice, quantity));
        } else {
            existing.increaseQuantity(quantity);
        }
        raise(new CartItemAdded(id, productId, quantity));
    }

    /**
     * 变更商品数量。
     */
    public void changeItemQuantity(ProductId productId, int quantity) {
        itemOrThrow(productId).changeQuantity(quantity);
    }

    /**
     * 移除商品。
     */
    public void removeItem(ProductId productId) {
        if (!items.removeIf(item -> item.productId().equals(productId))) {
            throw new DomainException("购物车中不存在该商品");
        }
    }

    /**
     * 清空购物车。
     */
    public void clear() {
        items.clear();
    }

    private CartItem itemOrThrow(ProductId productId) {
        CartItem item = itemOf(productId);
        if (item == null) {
            throw new DomainException("购物车中不存在该商品");
        }
        return item;
    }

    private CartItem itemOf(ProductId productId) {
        return items.stream()
                .filter(item -> item.productId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 供工厂使用的可变购物车项列表构建器。
     */
    static List<CartItem> newItemList() {
        return new ArrayList<>();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Cart that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
