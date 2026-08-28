package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.attribute.Attribute;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeFactory;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeId;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeType;
import wang.liangchen.matrix.shop.product.domain.port.AttributeRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * 属性仓储适配器：实现属性仓储端口，完成属性聚合与持久化对象之间的防腐翻译，
 * 重建聚合时委托属性工厂的reconstitute方法；查询读侧返回聚合根。
 */
@Repository
@Adapter(PortType.Repository)
public class AttributeRepositoryAdapter implements AttributeRepositoryPort, IRepositoryAdapter {

    private final AttributeDao attributeDao;
    private final AttributeFactory attributeFactory = new AttributeFactory();

    public AttributeRepositoryAdapter(AttributeDao attributeDao) {
        this.attributeDao = attributeDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Attribute> findById(AttributeId attributeId) {
        return attributeDao.findById(attributeId.value()).map(this::reconstitute);
    }

    @Override
    public void save(Attribute attribute) {
        attributeDao.save(toPo(attribute));
    }

    @Override
    public void remove(Attribute attribute) {
        attributeDao.deleteById(attribute.id().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attribute> findAll() {
        return attributeDao.findAllByOrderByNameAsc().stream()
                .map(this::reconstitute)
                .toList();
    }

    private Attribute reconstitute(AttributePo po) {
        return attributeFactory.reconstitute(AttributeId.of(po.getId()), po.getName(),
                AttributeType.valueOf(po.getType()), po.getOptions());
    }

    private AttributePo toPo(Attribute attribute) {
        AttributePo po = new AttributePo();
        po.setId(attribute.id().value());
        po.setName(attribute.name());
        po.setType(attribute.type().name());
        po.setOptions(attribute.options());
        return po;
    }
}
