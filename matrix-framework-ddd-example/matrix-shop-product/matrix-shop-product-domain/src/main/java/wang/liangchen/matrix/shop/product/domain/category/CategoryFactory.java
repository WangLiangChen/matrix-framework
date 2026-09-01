package wang.liangchen.matrix.shop.product.domain.category;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.factory.AbstractDomainFactory;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

/**
 * 类目工厂：封装类目的创建与重建逻辑。
 */
@DomainModel(DomainMetaModel.DomainFactory)
public final class CategoryFactory extends AbstractDomainFactory {

    /**
     * 创建全新的类目聚合。
     */
    public Category create(String name, CategoryId parentId) {
        if (name == null || name.isBlank()) {
            throw new DomainException("类目名称不能为空");
        }
        Category category = new Category(CategoryId.generate(), name, parentId);
        category.created();
        return category;
    }

    /**
     * 从持久化数据重建类目聚合。
     */
    public Category reconstitute(CategoryId id, String name, CategoryId parentId) {
        return new Category(id, name, parentId);
    }
}
