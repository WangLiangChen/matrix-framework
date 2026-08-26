package wang.liangchen.matrix.shop.product.domain.product;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AbstractAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.shop.product.domain.brand.BrandId;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;
import wang.liangchen.matrix.shop.product.domain.readmodel.SkuSummary;

import java.util.List;

/**
 * 商品聚合根(SPU)：标准产品单元，聚合内部包含多个SKU实体；
 * 通过身份标识引用类目(CategoryId)、品牌(BrandId)与属性(AttributeId)聚合，
 * 聚合外部只能通过本聚合根访问与修改内部状态。
 */
@DomainModel(DomainMetaModel.AggregateRoot)
public final class Product extends AbstractAggregateRoot<ProductId> implements IAggregateRoot<ProductId> {

    @Identity
    private final ProductId id;
    private final String name;
    private final String subtitle;
    private final CategoryId categoryId;
    private final BrandId brandId;
    private final List<AttributeValueRef> attributeValues;
    private final List<Sku> skus;
    private boolean listed;

    Product(ProductId id, String name, String subtitle, CategoryId categoryId, BrandId brandId,
            List<AttributeValueRef> attributeValues, List<Sku> skus, boolean listed) {
        this.id = id;
        this.name = name;
        this.subtitle = subtitle;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.attributeValues = List.copyOf(attributeValues);
        this.skus = skus;
        this.listed = listed;
    }

    public ProductId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String subtitle() {
        return subtitle;
    }

    public CategoryId categoryId() {
        return categoryId;
    }

    public BrandId brandId() {
        return brandId;
    }

    public boolean listed() {
        return listed;
    }

    public List<AttributeValueRef> attributeValues() {
        return attributeValues;
    }

    public List<SkuSummary> skuSummaries() {
        return skus.stream()
                .map(sku -> new SkuSummary(sku.id(), sku.attributeValues(), sku.price(), sku.stock()))
                .toList();
    }

    /**
     * 收集商品创建领域事实：仅由商品工厂在创建新商品时调用
     * （AbstractAggregateRoot#raise为受保护成员，事件由聚合自身收集）。
     */
    void created() {
        raise(new ProductCreatedEvent(id, name));
    }

    /**
     * 商品上架：商品进入可售状态。
     */
    public void putOnSale() {
        if (this.listed) {
            return;
        }
        this.listed = true;
        raise(new ProductListedEvent(id));
    }

    /**
     * 商品下架：商品退出可售状态。
     */
    public void takeOffSale() {
        if (!this.listed) {
            return;
        }
        this.listed = false;
        raise(new ProductDelistedEvent(id));
    }

    /**
     * 调整SKU销售价格。
     */
    public void changeSkuPrice(SkuId skuId, Money price) {
        Sku sku = skuOf(skuId);
        sku.changePrice(price);
        raise(new SkuPriceChangedEvent(id, skuId, price));
    }

    /**
     * 增加SKU库存。
     */
    public void increaseSkuStock(SkuId skuId, int quantity) {
        skuOf(skuId).increaseStock(quantity);
    }

    /**
     * 扣减SKU库存。
     */
    public void decreaseSkuStock(SkuId skuId, int quantity) {
        skuOf(skuId).decreaseStock(quantity);
    }

    private Sku skuOf(SkuId skuId) {
        return skus.stream()
                .filter(sku -> sku.id().equals(skuId))
                .findFirst()
                .orElseThrow(() -> new DomainException("商品中不存在SKU：" + skuId.value()));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Product that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
