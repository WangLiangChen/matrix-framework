package wang.liangchen.matrix.shop.product.domain.brand;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.factory.AbstractDomainFactory;

/**
 * 品牌工厂：委托品牌聚合自身的静态工厂方法，保留工厂角色以兼容既有调用方。
 */
@DomainModel(DomainMetaModel.DomainFactory)
public final class BrandFactory extends AbstractDomainFactory {

    /**
     * 创建全新的品牌聚合。
     */
    public Brand create(String name, String description, String logo) {
        return Brand.create(name, description, logo);
    }

    /**
     * 从持久化数据重建品牌聚合。
     */
    public Brand reconstitute(BrandId id, String name, String description, String logo) {
        return Brand.reconstitute(id, name, description, logo);
    }
}