package wang.liangchen.matrix.shop.product.domain.category;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AbstractAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

/**
 * 类目聚合根：表达商品类目，通过parentId引用父类目形成层级结构，
 * 同一层级的类目名称在上下文中保持语义一致。
 */
@DomainModel(DomainMetaModel.AggregateRoot)
public final class Category extends AbstractAggregateRoot<CategoryId> implements IAggregateRoot<CategoryId> {

    @Identity
    private final CategoryId id;
    private String name;
    private CategoryId parentId;

    Category(CategoryId id, String name, CategoryId parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
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
     * 收集类目创建领域事实：仅由类目工厂在创建新类目时调用
     * （AbstractAggregateRoot#raise为受保护成员，事件由聚合自身收集）。
     */
    void created() {
        raise(new CategoryCreated(id, name));
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
        raise(new CategoryMoved(id, newParentId));
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
