package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/** 违规：领域事件类未声明final（不可变） */
@DomainModel(DomainMetaModel.DomainEvent)
public class BadMutableEvent extends AbstractDomainEvent {

    private final String data;

    public BadMutableEvent(String data) {
        super();
        this.data = data;
    }
}
