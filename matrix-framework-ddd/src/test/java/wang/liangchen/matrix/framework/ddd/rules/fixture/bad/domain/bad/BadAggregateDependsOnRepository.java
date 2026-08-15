package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AbstractAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.framework.ddd.domain.identity.UUIDIdentity;
import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;

/** 违规：聚合根依赖端口（不能在聚合内部使用资源库等外部资源） */
@DomainModel(DomainMetaModel.AggregateRoot)
public final class BadAggregateDependsOnRepository extends AbstractAggregateRoot<UUIDIdentity> implements IAggregateRoot<UUIDIdentity> {

    @Identity
    private final UUIDIdentity id;

    @SuppressWarnings("unused")
    private final IRepositoryPort<?, ?> repository;

    public BadAggregateDependsOnRepository(UUIDIdentity id, IRepositoryPort<?, ?> repository) {
        this.id = id;
        this.repository = repository;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BadAggregateDependsOnRepository that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
