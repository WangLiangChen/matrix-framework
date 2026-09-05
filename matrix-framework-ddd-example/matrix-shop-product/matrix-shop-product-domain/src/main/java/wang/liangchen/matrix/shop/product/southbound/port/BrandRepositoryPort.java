package wang.liangchen.matrix.shop.product.southbound.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.brand.Brand;
import wang.liangchen.matrix.shop.product.domain.brand.BrandId;

import java.util.List;

/**
 * 品牌仓储端口：以品牌聚合根为读写单位（findById/save/remove），实现位于南向适配层；
 * 查询读侧经本端口承担，返回聚合根供查询用例只读使用。
 */
@Port(PortType.Repository)
public interface BrandRepositoryPort extends IRepositoryPort<BrandId, Brand> {

    /**
     * 查询全部品牌，供查询用例只读使用。
     */
    List<Brand> findAll();
}
