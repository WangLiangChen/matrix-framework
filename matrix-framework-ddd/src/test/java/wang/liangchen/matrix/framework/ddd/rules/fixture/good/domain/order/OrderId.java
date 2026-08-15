package wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.AbstractIdentity;
import wang.liangchen.matrix.framework.ddd.domain.identity.IStringIdentity;

/** 合规身份标识（领域类型） */
@DomainModel(DomainMetaModel.Identity)
public final class OrderId extends AbstractIdentity implements IStringIdentity {

    private final String value;

    private OrderId(String value) {
        this.value = value;
    }

    public static OrderId of(String value) {
        return new OrderId(value);
    }

    @Override
    public String value() {
        return value;
    }
}
