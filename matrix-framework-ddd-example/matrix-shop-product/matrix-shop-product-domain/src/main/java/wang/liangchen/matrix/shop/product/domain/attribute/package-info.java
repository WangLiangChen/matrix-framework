/**
 * 属性聚合：商品的属性定义，按用途分为一般属性、关键属性与销售属性；
 * 商品聚合通过属性标识(AttributeId)引用属性并携带属性值。
 */
@AggregatePackage(name = "attribute")
package wang.liangchen.matrix.shop.product.domain.attribute;

import wang.liangchen.matrix.framework.ddd.domain.aggregate.AggregatePackage;
