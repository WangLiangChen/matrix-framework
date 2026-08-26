package wang.liangchen.matrix.shop.order.domain.cart;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.entity.AbstractEntity;
import wang.liangchen.matrix.framework.ddd.domain.entity.IEntity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.domain.order.Money;
import wang.liangchen.matrix.shop.order.domain.order.ProductId;

/**
 * 购物车项：购物车聚合内部实体，以商品标识为身份标识，
 * 商品名称与单价为加入购物车时的快照，同一商品只存在一个购物车项。
 */
@DomainModel(DomainMetaModel.Entity)
final class CartItem extends AbstractEntity<ProductId> implements IEntity<ProductId> {

    @Identity
    private final ProductId productId;
    private final String productName;
    private final Money unitPrice;
    private int quantity;

    private CartItem(ProductId productId, String productName, Money unitPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    static CartItem of(ProductId productId, String productName, Money unitPrice, int quantity) {
        if (productName == null || productName.isBlank()) {
            throw new DomainException("商品名称不能为空");
        }
        if (unitPrice == null || !unitPrice.isPositive()) {
            throw new DomainException("商品单价必须大于零");
        }
        if (quantity <= 0) {
            throw new DomainException("商品数量必须大于零");
        }
        return new CartItem(productId, productName, unitPrice, quantity);
    }

    ProductId productId() {
        return productId;
    }

    String productName() {
        return productName;
    }

    Money unitPrice() {
        return unitPrice;
    }

    int quantity() {
        return quantity;
    }

    void increaseQuantity(int quantity) {
        if (quantity <= 0) {
            throw new DomainException("商品数量必须大于零");
        }
        this.quantity += quantity;
    }

    void changeQuantity(int quantity) {
        if (quantity <= 0) {
            throw new DomainException("商品数量必须大于零");
        }
        this.quantity = quantity;
    }

    Money subtotal() {
        return unitPrice.multiply(quantity);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CartItem that)) {
            return false;
        }
        return productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return productId.hashCode();
    }
}
