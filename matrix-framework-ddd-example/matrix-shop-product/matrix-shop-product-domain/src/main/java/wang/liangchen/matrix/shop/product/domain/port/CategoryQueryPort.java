package wang.liangchen.matrix.shop.product.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.category.Category;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.readmodel.CategorySummary;

import java.util.List;

/**
 * 类目查询端口：CQRS查询侧，只读访问类目读模型。
 */
@Port(PortType.Repository)
public interface CategoryQueryPort extends IRepositoryPort<CategoryId, Category> {

    List<CategorySummary> queryCategoryTree();
}
