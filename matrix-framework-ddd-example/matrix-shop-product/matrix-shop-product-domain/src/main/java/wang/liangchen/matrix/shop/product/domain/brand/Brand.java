package wang.liangchen.matrix.shop.product.domain.brand;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AbstractAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

/**
 * 品牌聚合根：表达商品的品牌及其描述信息，
 * 商品聚合通过品牌标识(BrandId)引用品牌。
 */
@DomainModel(DomainMetaModel.AggregateRoot)
public final class Brand extends AbstractAggregateRoot<BrandId> implements IAggregateRoot<BrandId> {

    @Identity
    private final BrandId brandId;
    private String name;
    private String description;
    private String logo;

    Brand(BrandId brandId, String name, String description, String logo) {
        this.brandId = brandId;
        this.name = name;
        this.description = description;
        this.logo = logo;
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
     * 收集品牌创建领域事实：仅由品牌工厂在创建新品牌时调用
     * （AbstractAggregateRoot#raise为受保护成员，事件由聚合自身收集）。
     */
    void created() {
        raise(new BrandCreated(brandId, name));
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
