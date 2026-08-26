/**
 * 购物车聚合：以购物车为聚合根，购物车项为聚合内部实体，
 * 通过商品标识(ProductId)引用商品聚合，商品名称与单价为加入购物车时的快照。
 */
@AggregatePackage(name = "cart")
package wang.liangchen.matrix.shop.order.domain.cart;

import wang.liangchen.matrix.framework.ddd.domain.aggregate.AggregatePackage;
