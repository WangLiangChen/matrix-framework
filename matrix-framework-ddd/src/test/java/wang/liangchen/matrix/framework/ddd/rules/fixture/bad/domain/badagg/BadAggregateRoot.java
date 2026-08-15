package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.badagg;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AbstractAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;

/** 违规：聚合根所在包未标注@AggregatePackage */
@DomainModel(DomainMetaModel.AggregateRoot)
public final class BadAggregateRoot extends AbstractAggregateRoot<BadAggId> implements IAggregateRoot<BadAggId> {

    @Identity
    private final BadAggId badAggId;

    public BadAggregateRoot(BadAggId badAggId) {
        this.badAggId = badAggId;
    }
}
