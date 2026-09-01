package wang.liangchen.matrix.shop.product.domain.category;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/**
 * 类目已创建：类目进入商品目录的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class CategoryCreatedEvent extends AbstractDomainEvent {

    private final CategoryId categoryId;
    private final String categoryName;

    public CategoryCreatedEvent(CategoryId categoryId, String categoryName) {
        super();
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public CategoryId categoryId() {
        return categoryId;
    }

    public String categoryName() {
        return categoryName;
    }
}