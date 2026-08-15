package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/** 违规：领域事件实例字段未声明final（不可变） */
@DomainModel(DomainMetaModel.DomainEvent)
public final class BadEventWithMutableField extends AbstractDomainEvent {

    private String data;

    public BadEventWithMutableField(String data) {
        super();
        this.data = data;
    }
}
