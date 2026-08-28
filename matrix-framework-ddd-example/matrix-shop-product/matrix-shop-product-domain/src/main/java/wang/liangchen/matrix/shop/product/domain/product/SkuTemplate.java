package wang.liangchen.matrix.shop.product.domain.product;

import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

import java.util.List;
import java.util.Objects;

/**
 * SKU模板：创建商品时用于组装SKU的原始信息。
 * 携带属性值集合（防御性拷贝的不可变record），
 * 不标记为值对象（值对象字段须为深度不可变类型，集合类型不满足）。
 */
public record SkuTemplate(List<AttributeValueRef> attributeValues, Money price, int stock) {

    public SkuTemplate {
        Objects.requireNonNull(attributeValues, "SKU属性值不能为空");
        Objects.requireNonNull(price, "SKU价格不能为空");
        if (!price.isPositive()) {
            throw new DomainException("SKU价格必须大于零");
        }
        if (stock < 0) {
            throw new DomainException("SKU库存不能为负数");
        }
        attributeValues = List.copyOf(attributeValues);
    }
}
