package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.entity.AbstractEntity;
import wang.liangchen.matrix.framework.ddd.domain.entity.IEntity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;

/**
 * 订单项：订单聚合内部实体，以下单时的商品快照（名称、单价）表达交易内容，
 * 每个商品在订单中最多出现一次，以商品标识为身份标识。
 */
@DomainModel(DomainMetaModel.Entity)
final class OrderItem extends AbstractEntity<ProductId> implements IEntity<ProductId> {

    @Identity
    private final ProductId productId;
    private final String productName;
    private final Money unitPrice;
    private final int quantity;

    private OrderItem(ProductId productId, String productName, Money unitPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    static OrderItem of(ProductId productId, String productName, Money unitPrice, int quantity) {
        if (productName == null || productName.isBlank()) {
            throw new DomainException("商品名称不能为空");
        }
        if (unitPrice == null || !unitPrice.isPositive()) {
            throw new DomainException("商品单价必须大于零");
        }
        if (quantity <= 0) {
            throw new DomainException("商品数量必须大于零");
        }
        return new OrderItem(productId, productName, unitPrice, quantity);
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

    Money subtotal() {
        return unitPrice.multiply(quantity);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OrderItem that)) {
            return false;
        }
        return productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return productId.hashCode();
    }
}
