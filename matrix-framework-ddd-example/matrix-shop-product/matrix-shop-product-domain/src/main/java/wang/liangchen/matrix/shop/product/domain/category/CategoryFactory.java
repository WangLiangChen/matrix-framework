package wang.liangchen.matrix.shop.product.domain.category;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.factory.AbstractDomainFactory;

/**
 * 类目工厂：委托类目聚合自身的静态工厂方法，保留工厂角色以兼容既有调用方。
 */
@DomainModel(DomainMetaModel.DomainFactory)
public final class CategoryFactory extends AbstractDomainFactory {

    /**
     * 创建全新的类目聚合。
     */
    public Category create(String name, CategoryId parentId) {
        return Category.create(name, parentId);
    }

    /**
     * 从持久化数据重建类目聚合。
     */
    public Category reconstitute(CategoryId id, String name, CategoryId parentId) {
        return Category.reconstitute(id, name, parentId);
    }
}