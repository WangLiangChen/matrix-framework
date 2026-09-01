package wang.liangchen.matrix.shop.product.domain.product;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.entity.AbstractEntity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

import java.util.List;

/**
 * SKU：商品聚合内部实体，表达一个具体可售的销售单元（如"黑色/64G"），
 * 拥有独立的价格与库存，生命周期依附于商品聚合根。
 */
@DomainModel(DomainMetaModel.Entity)
final class Sku extends AbstractEntity<SkuId> {

    @Identity
    private final SkuId id;
    private final List<AttributeValue> attributeValues;
    private Money price;
    private int stock;

    private Sku(SkuId id, List<AttributeValue> attributeValues, Money price, int stock) {
        this.id = id;
        this.attributeValues = attributeValues;
        this.price = price;
        this.stock = stock;
    }

    static Sku of(SkuId id, List<AttributeValue> attributeValues, Money price, int stock) {
        if (price == null || !price.isPositive()) {
            throw new DomainException("SKU价格必须大于零");
        }
        if (stock < 0) {
            throw new DomainException("SKU库存不能为负数");
        }
        return new Sku(id, List.copyOf(attributeValues), price, stock);
    }

    SkuId id() {
        return id;
    }

    List<AttributeValue> attributeValues() {
        return attributeValues;
    }

    Money price() {
        return price;
    }

    int stock() {
        return stock;
    }

    void changePrice(Money newPrice) {
        if (newPrice == null || !newPrice.isPositive()) {
            throw new DomainException("SKU价格必须大于零");
        }
        this.price = newPrice;
    }

    void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new DomainException("库存增量必须大于零");
        }
        this.stock += quantity;
    }

    void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new DomainException("库存扣减量必须大于零");
        }
        if (this.stock < quantity) {
            throw new DomainException("SKU库存不足");
        }
        this.stock -= quantity;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Sku that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
