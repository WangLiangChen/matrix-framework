package wang.liangchen.matrix.shop.product.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.product.Product;
import wang.liangchen.matrix.shop.product.domain.product.ProductId;
import wang.liangchen.matrix.shop.product.domain.readmodel.ProductDetail;
import wang.liangchen.matrix.shop.product.domain.readmodel.ProductSummary;

import java.util.List;
import java.util.Optional;

/**
 * 商品查询端口：CQRS查询侧，只读访问商品读模型。
 */
@Port(PortType.Repository)
public interface ProductQueryPort extends IRepositoryPort<ProductId, Product> {

    Optional<ProductDetail> queryById(ProductId productId);

    List<ProductSummary> queryProducts(String keyword, CategoryId categoryId);
}
