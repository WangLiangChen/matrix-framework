package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.entity.IEntity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.framework.ddd.domain.identity.UUIDIdentity;

/** 违规：类名以技术后缀Entity结尾 */
@DomainModel(DomainMetaModel.Entity)
public final class BadEntity implements IEntity<UUIDIdentity> {

    @Identity
    private final UUIDIdentity id;

    public BadEntity(UUIDIdentity id) {
        this.id = id;
    }
}
