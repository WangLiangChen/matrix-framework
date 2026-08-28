package wang.liangchen.matrix.shop.product.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.category.Category;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;

import java.util.List;

/**
 * 类目仓储端口：以类目聚合根为读写单位（findById/save/remove），实现位于南向适配层；
 * 查询读侧经本端口承担，返回聚合根供查询用例只读使用。
 */
@Port(PortType.Repository)
public interface CategoryRepositoryPort extends IRepositoryPort<CategoryId, Category> {

    /**
     * 查询全部类目（平铺列表），供查询用例只读装配类目树。
     */
    List<Category> findAll();
}
