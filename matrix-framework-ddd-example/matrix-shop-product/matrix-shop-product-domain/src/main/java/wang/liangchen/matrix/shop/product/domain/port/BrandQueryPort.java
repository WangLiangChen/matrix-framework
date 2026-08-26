package wang.liangchen.matrix.shop.product.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.brand.Brand;
import wang.liangchen.matrix.shop.product.domain.brand.BrandId;
import wang.liangchen.matrix.shop.product.domain.readmodel.BrandSummary;

import java.util.List;

/**
 * 品牌查询端口：CQRS查询侧，只读访问品牌读模型。
 */
@Port(PortType.Repository)
public interface BrandQueryPort extends IRepositoryPort<BrandId, Brand> {

    List<BrandSummary> queryAllBrands();
}
