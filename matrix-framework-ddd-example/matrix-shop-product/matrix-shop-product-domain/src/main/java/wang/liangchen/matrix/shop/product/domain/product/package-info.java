/**
 * 商品聚合：以SPU为聚合根，SKU为聚合内部实体；
 * 通过身份标识引用类目(CategoryId)、品牌(BrandId)与属性(AttributeId)聚合。
 */
@AggregatePackage(name = "product")
package wang.liangchen.matrix.shop.product.domain.product;

import wang.liangchen.matrix.framework.ddd.domain.aggregate.AggregatePackage;
