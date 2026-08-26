package wang.liangchen.matrix.shop.product.domain.port;

import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.attribute.Attribute;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeId;
import wang.liangchen.matrix.shop.product.domain.readmodel.AttributeSummary;

import java.util.List;

/**
 * 属性查询端口：CQRS查询侧，只读访问属性读模型。
 */
@Port(PortType.Repository)
public interface AttributeQueryPort extends IRepositoryPort<AttributeId, Attribute> {

    List<AttributeSummary> queryAllAttributes();
}
