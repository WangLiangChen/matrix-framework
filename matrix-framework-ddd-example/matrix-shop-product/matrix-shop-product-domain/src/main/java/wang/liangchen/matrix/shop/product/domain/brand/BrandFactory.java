package wang.liangchen.matrix.shop.product.domain.brand;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.factory.IDomainFactory;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

/**
 * 品牌工厂：封装品牌的创建与重建逻辑。
 */
@DomainModel(DomainMetaModel.DomainFactory)
public final class BrandFactory implements IDomainFactory {

    /**
     * 创建全新的品牌聚合。
     */
    public Brand create(String name, String description, String logo) {
        if (name == null || name.isBlank()) {
            throw new DomainException("品牌名称不能为空");
        }
        Brand brand = new Brand(BrandId.generate(), name, description, logo);
        brand.created();
        return brand;
    }

    /**
     * 从持久化数据重建品牌聚合。
     */
    public Brand reconstitute(BrandId id, String name, String description, String logo) {
        return new Brand(id, name, description, logo);
    }
}
