package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.badagg;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.factory.IDomainFactory;

/** 领域工厂：其存在使同包聚合根BadAggregateRoot的public构造成为违规（聚合根只能经工厂创建） */
@DomainModel(DomainMetaModel.DomainFactory)
public final class BadAggregateFactory implements IDomainFactory {

    public BadAggregateRoot create(BadAggId badAggId) {
        return new BadAggregateRoot(badAggId);
    }
}
