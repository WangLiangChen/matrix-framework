package wang.liangchen.matrix.shop.product.domain.attribute;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/**
 * 属性已创建：属性进入商品目录的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class AttributeCreated extends AbstractDomainEvent {

    private final AttributeId attributeId;
    private final String attributeName;
    private final AttributeType attributeType;

    public AttributeCreated(AttributeId attributeId, String attributeName, AttributeType attributeType) {
        super();
        this.attributeId = attributeId;
        this.attributeName = attributeName;
        this.attributeType = attributeType;
    }

    public AttributeId attributeId() {
        return attributeId;
    }

    public String attributeName() {
        return attributeName;
    }

    public AttributeType attributeType() {
        return attributeType;
    }
}
