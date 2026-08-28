package wang.liangchen.matrix.shop.product.domain.product;

import java.util.List;

/**
 * SKU摘要：商品聚合对外暴露的SKU快照，同时作为商品工厂重建SKU的入参。
 * 携带属性值集合（防御性拷贝的不可变record），
 * 不标记为值对象（值对象字段须为深度不可变类型，集合类型不满足）。
 */
public record SkuSummary(SkuId id, List<AttributeValueRef> attributeValues, Money price, int stock) {

    public SkuSummary {
        attributeValues = List.copyOf(attributeValues);
    }
}
