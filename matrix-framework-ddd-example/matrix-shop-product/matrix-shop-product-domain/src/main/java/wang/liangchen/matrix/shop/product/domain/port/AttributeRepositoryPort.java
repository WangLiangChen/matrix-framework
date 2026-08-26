package wang.liangchen.matrix.shop.product.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.attribute.Attribute;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeId;

/**
 * 属性仓储端口：以属性聚合根为读写单位（findById/save/remove），实现位于南向适配层。
 */
@Port(PortType.Repository)
public interface AttributeRepositoryPort extends IRepositoryPort<AttributeId, Attribute> {
}
