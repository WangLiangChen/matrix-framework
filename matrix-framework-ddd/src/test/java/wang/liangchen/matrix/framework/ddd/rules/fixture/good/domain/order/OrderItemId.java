package wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.AbstractIdentity;
import wang.liangchen.matrix.framework.ddd.domain.identity.ILongIdentity;

/** 合规身份标识（Long值） */
@DomainModel(DomainMetaModel.Identity)
public final class OrderItemId extends AbstractIdentity implements ILongIdentity {

    private final Long value;

    private OrderItemId(Long value) {
        this.value = value;
    }

    public static OrderItemId of(Long value) {
        return new OrderItemId(value);
    }

    @Override
    public Long value() {
        return value;
    }
}
