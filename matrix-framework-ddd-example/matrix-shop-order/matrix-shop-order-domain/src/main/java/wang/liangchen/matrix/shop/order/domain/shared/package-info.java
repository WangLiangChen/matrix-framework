/**
 * 上下文内跨聚合共享的值对象：订单上下文内订单与购物车两个聚合共享的值对象与身份标识
 * （金额、商品身份标识、商品摘要、交易项摘要），不属于任何单一聚合。
 * （注：此为限界上下文内部跨聚合的值对象共享，并非上下文映射中的"共享内核"模式。）
 */
@DomainPackage
package wang.liangchen.matrix.shop.order.domain.shared;

import wang.liangchen.matrix.framework.ddd.domain.DomainPackage;
