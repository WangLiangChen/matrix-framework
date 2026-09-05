package wang.liangchen.matrix.shop.product.domain.category;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AbstractAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

/**
 * 类目聚合根：表达商品类目，通过parentId引用父类目形成层级结构，
 * 同一层级的类目名称在上下文中保持语义一致。
 */
@DomainModel(DomainMetaModel.AggregateRoot)
public final class Category extends AbstractAggregateRoot<CategoryId> {

    @Identity
    private final CategoryId id;
    private String name;
    private CategoryId parentId;

    private Category(CategoryId id, String name, CategoryId parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    /**
     * 创建全新的类目聚合（聚合自身担任工厂）。
     */
    public static Category create(String name, CategoryId parentId) {
        if (name == null || name.isBlank()) {
            throw new DomainException("类目名称不能为空");
        }
        Category category = new Category(CategoryId.generate(), name, parentId);
        category.raise(new CategoryCreatedEvent(category.id, category.name));
        return category;
    }

    /**
     * 从持久化数据重建类目聚合（仓储委托重建）。
     */
    public static Category reconstitute(CategoryId id, String name, CategoryId parentId) {
        return new Category(id, name, parentId);
    }

    public CategoryId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public CategoryId parentId() {
        return parentId;
    }

    /**
     * 重命名类目。
     */
    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new DomainException("类目名称不能为空");
        }
        this.name = newName;
    }

    /**
     * 移动类目：变更父类目。
     */
    public void moveTo(CategoryId newParentId) {
        if (id.equals(newParentId)) {
            throw new DomainException("类目不能移动到自身");
        }
        this.parentId = newParentId;
        raise(new CategoryMovedEvent(id, newParentId));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Category that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}