/**
 * 订单聚合：以订单为聚合根，订单项为聚合内部实体，
 * 商品信息（名称、单价）在下单时快照，通过商品标识(ProductId)引用商品聚合。
 */
@AggregatePackage(name = "order")
package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.aggregate.AggregatePackage;
