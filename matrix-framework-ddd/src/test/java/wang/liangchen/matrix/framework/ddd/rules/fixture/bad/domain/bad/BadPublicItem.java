package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.entity.IEntity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.framework.ddd.domain.identity.UUIDIdentity;

/** 违规：@AggregatePackage包内的非聚合根实体声明为public */
@DomainModel(DomainMetaModel.Entity)
public final class BadPublicItem implements IEntity<UUIDIdentity> {

    @Identity
    private final UUIDIdentity id;

    public BadPublicItem(UUIDIdentity id) {
        this.id = id;
    }
}
