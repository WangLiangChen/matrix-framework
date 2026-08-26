package wang.liangchen.matrix.shop.product.domain.attribute;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.factory.IDomainFactory;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

import java.util.List;

/**
 * 属性工厂：封装属性的创建与重建逻辑。
 */
@DomainModel(DomainMetaModel.DomainFactory)
public final class AttributeFactory implements IDomainFactory {

    /**
     * 创建全新的属性聚合。
     */
    public Attribute create(String name, AttributeType type, List<String> options) {
        if (name == null || name.isBlank()) {
            throw new DomainException("属性名称不能为空");
        }
        if (type == null) {
            throw new DomainException("属性类型不能为空");
        }
        Attribute attribute = new Attribute(AttributeId.generate(), name, type, options);
        attribute.created();
        return attribute;
    }

    /**
     * 从持久化数据重建属性聚合。
     */
    public Attribute reconstitute(AttributeId id, String name, AttributeType type, List<String> options) {
        return new Attribute(id, name, type, options);
    }
}
