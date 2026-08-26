/**
 * 订单限界上下文。
 * 核心域：维护订单交易统一语言，包含订单(order)、购物车(cart)两个聚合。
 * 作为商品上下文的下游（客户-供应商模式），通过防腐层(IClientPort)翻译商品上下文
 * 的发布语言，避免商品模型腐化本上下文的领域模型。
 */
@BoundedContextPackage(name = "order", domainType = DomainType.Core)
package wang.liangchen.matrix.shop.order;

import wang.liangchen.matrix.framework.ddd.BoundedContextPackage;
import wang.liangchen.matrix.framework.ddd.DomainType;
