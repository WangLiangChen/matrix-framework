package wang.liangchen.matrix.shop.product.domain.product;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeId;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

import java.util.Objects;

/**
 * 属性值引用：商品（或SKU）对属性聚合中某个属性的取值，
 * 一般属性描述SPU，销售属性描述SKU的规格。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record AttributeValueRef(AttributeId attributeId, String value) implements IValueObject {

    public AttributeValueRef {
        Objects.requireNonNull(attributeId, "属性标识不能为空");
        if (value == null || value.isBlank()) {
            throw new DomainException("属性值不能为空");
        }
    }
}
