package wang.liangchen.matrix.shop.product.domain.brand;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AbstractAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

/**
 * 品牌聚合根：表达商品的品牌及其描述信息，
 * 商品聚合通过品牌标识(BrandId)引用品牌。
 */
@DomainModel(DomainMetaModel.AggregateRoot)
public final class Brand extends AbstractAggregateRoot<BrandId> {

    @Identity
    private final BrandId brandId;
    private String name;
    private String description;
    private String logo;

    private Brand(BrandId brandId, String name, String description, String logo) {
        this.brandId = brandId;
        this.name = name;
        this.description = description;
        this.logo = logo;
    }

    /**
     * 创建全新的品牌聚合（聚合自身担任工厂）。
     */
    public static Brand create(String name, String description, String logo) {
        if (name == null || name.isBlank()) {
            throw new DomainException("品牌名称不能为空");
        }
        Brand brand = new Brand(BrandId.generate(), name, description, logo);
        brand.raise(new BrandCreatedEvent(brand.brandId, brand.name));
        return brand;
    }

    /**
     * 从持久化数据重建品牌聚合（仓储委托重建）。
     */
    public static Brand reconstitute(BrandId id, String name, String description, String logo) {
        return new Brand(id, name, description, logo);
    }

    public BrandId id() {
        return brandId;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public String logo() {
        return logo;
    }

    /**
     * 重命名品牌。
     */
    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new DomainException("品牌名称不能为空");
        }
        this.name = newName;
    }

    /**
     * 变更品牌描述。
     */
    public void changeDescription(String newDescription) {
        this.description = newDescription;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Brand that)) {
            return false;
        }
        return brandId.equals(that.brandId);
    }

    @Override
    public int hashCode() {
        return brandId.hashCode();
    }
}