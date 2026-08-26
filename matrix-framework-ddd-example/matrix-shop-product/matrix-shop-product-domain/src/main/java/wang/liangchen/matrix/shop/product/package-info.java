/**
 * 商品限界上下文。
 * 核心域：维护商品目录统一语言，包含商品(product/spu|sku)、类目(category)、品牌(brand)、属性(attribute)四个聚合。
 * 作为商品目录的上游上下文，通过开放主机服务（Resource）向订单等下游上下文提供发布语言。
 */
@BoundedContextPackage(name = "product", domainType = DomainType.Core)
package wang.liangchen.matrix.shop.product;

import wang.liangchen.matrix.framework.ddd.BoundedContextPackage;
import wang.liangchen.matrix.framework.ddd.DomainType;
