package wang.liangchen.matrix.shop.product.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.product.Product;
import wang.liangchen.matrix.shop.product.domain.product.ProductId;

/**
 * 商品仓储端口：以商品聚合根为读写单位（findById/save/remove），实现位于南向适配层。
 */
@Port(PortType.Repository)
public interface ProductRepositoryPort extends IRepositoryPort<ProductId, Product> {
}
