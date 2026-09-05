package wang.liangchen.matrix.shop.product.southbound.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.attribute.Attribute;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeId;

import java.util.List;

/**
 * 属性仓储端口：以属性聚合根为读写单位（findById/save/remove），实现位于南向适配层；
 * 查询读侧经本端口承担，返回聚合根供查询用例只读使用。
 */
@Port(PortType.Repository)
public interface AttributeRepositoryPort extends IRepositoryPort<AttributeId, Attribute> {

    /**
     * 查询全部属性，供查询用例只读使用。
     */
    List<Attribute> findAll();
}
