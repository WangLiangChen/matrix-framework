package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.badagg;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.AbstractIdentity;
import wang.liangchen.matrix.framework.ddd.domain.identity.IStringIdentity;

/** 聚合根身份标识（自身合规，供BadAggregateRoot使用） */
@DomainModel(DomainMetaModel.Identity)
public final class BadAggId extends AbstractIdentity implements IStringIdentity {

    private final String value;

    private BadAggId(String value) {
        this.value = value;
    }

    public static BadAggId of(String value) {
        return new BadAggId(value);
    }

    @Override
    public String value() {
        return value;
    }
}
