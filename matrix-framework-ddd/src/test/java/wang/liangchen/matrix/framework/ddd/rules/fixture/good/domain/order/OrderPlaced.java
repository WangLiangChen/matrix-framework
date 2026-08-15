package wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/** 合规领域事件（过去时命名、不可变） */
@DomainModel(DomainMetaModel.DomainEvent)
public final class OrderPlaced extends AbstractDomainEvent {

    private final String orderId;

    public OrderPlaced(String orderId) {
        super();
        this.orderId = orderId;
    }
}
