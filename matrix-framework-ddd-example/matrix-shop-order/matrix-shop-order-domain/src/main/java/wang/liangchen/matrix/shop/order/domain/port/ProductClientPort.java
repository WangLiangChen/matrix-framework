package wang.liangchen.matrix.shop.order.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IClientPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.order.domain.shared.ProductId;
import wang.liangchen.matrix.shop.order.domain.shared.ProductSummary;

/**
 * 商品客户端端口：订单上下文对商品上下文（上游）的防腐层端口，
 * 适配器实现内完成对商品上下文发布语言的翻译，返回订单领域的商品摘要。
 */
@Port(PortType.Client)
public interface ProductClientPort extends IClientPort {

    ProductSummary obtainProduct(ProductId productId);
}
