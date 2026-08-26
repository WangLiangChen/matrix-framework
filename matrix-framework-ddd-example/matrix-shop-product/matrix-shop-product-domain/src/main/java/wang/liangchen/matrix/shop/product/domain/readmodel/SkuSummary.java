package wang.liangchen.matrix.shop.product.domain.readmodel;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.product.domain.product.AttributeValueRef;
import wang.liangchen.matrix.shop.product.domain.product.Money;
import wang.liangchen.matrix.shop.product.domain.product.SkuId;

import java.util.List;

/**
 * SKU摘要：SKU读模型，同时作为商品工厂重建SKU的快照。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record SkuSummary(SkuId id, List<AttributeValueRef> attributeValues, Money price, int stock) implements IValueObject {
}
