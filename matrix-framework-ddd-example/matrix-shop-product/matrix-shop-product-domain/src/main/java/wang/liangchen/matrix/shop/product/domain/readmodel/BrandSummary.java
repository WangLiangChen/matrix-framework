package wang.liangchen.matrix.shop.product.domain.readmodel;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.product.domain.brand.BrandId;

/**
 * 品牌摘要：品牌列表读模型。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record BrandSummary(BrandId id, String name, String description, String logo) implements IValueObject {
}
