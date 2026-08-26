package wang.liangchen.matrix.shop.product.domain.readmodel;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeId;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeType;

import java.util.List;

/**
 * 属性摘要：属性列表读模型。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record AttributeSummary(AttributeId id, String name, AttributeType type, List<String> options) implements IValueObject {
}
