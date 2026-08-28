package wang.liangchen.matrix.shop.product.domain.category;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/**
 * 类目已移动：类目归属的父类目发生变更的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class CategoryMoved extends AbstractDomainEvent {

    private final CategoryId categoryId;
    private final CategoryId newParentId;

    public CategoryMoved(CategoryId categoryId, CategoryId newParentId) {
        super();
        this.categoryId = categoryId;
        this.newParentId = newParentId;
    }

    public CategoryId categoryId() {
        return categoryId;
    }

    public CategoryId newParentId() {
        return newParentId;
    }
}
