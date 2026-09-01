package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

import java.util.List;

/** 违规：领域事件字段类型可变。 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class BadEventWithMutableType extends AbstractDomainEvent {

    private final List<String> data;

    public BadEventWithMutableType(List<String> data) {
        this.data = data;
    }
}
