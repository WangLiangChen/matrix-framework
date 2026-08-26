package wang.liangchen.matrix.shop.product.domain.readmodel;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.product.domain.brand.BrandId;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.product.AttributeValueRef;
import wang.liangchen.matrix.shop.product.domain.product.ProductId;

import java.util.List;

/**
 * 商品明细：商品详情页读模型。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record ProductDetail(ProductId id, String name, String subtitle,
                            CategoryId categoryId, BrandId brandId, boolean listed,
                            List<AttributeValueRef> attributeValues, List<SkuSummary> skus) implements IValueObject {
}
