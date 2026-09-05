package wang.liangchen.matrix.shop.product.southbound.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.product.Product;
import wang.liangchen.matrix.shop.product.domain.product.ProductId;

import java.util.List;

/**
 * 商品仓储端口：以商品聚合根为读写单位（findById/save/remove），实现位于南向适配层；
 * 查询读侧经本端口承担，统一语言命名的查询方法返回聚合根，供查询用例只读使用。
 */
@Port(PortType.Repository)
public interface ProductRepositoryPort extends IRepositoryPort<ProductId, Product> {

    /**
     * 查询已上架商品：按关键词（名称或副标题）与类目过滤，返回商品聚合根列表。
     */
    List<Product> findListed(String keyword, CategoryId categoryId);
}
