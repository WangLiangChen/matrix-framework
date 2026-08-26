package wang.liangchen.matrix.shop.product.domain.readmodel;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.product.domain.brand.BrandId;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.product.ProductId;

/**
 * 商品摘要：商品列表读模型。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record ProductSummary(ProductId id, String name, String subtitle,
                             CategoryId categoryId, BrandId brandId, boolean listed) implements IValueObject {
}
