package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.entity.IEntity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.framework.ddd.domain.identity.UUIDIdentity;

/** 违规：@Identity字段类型为裸String而非IIdentity值对象 */
@DomainModel(DomainMetaModel.Entity)
public final class BadIdentityType implements IEntity<UUIDIdentity> {

    @Identity
    private final String id;

    public BadIdentityType(String id) {
        this.id = id;
    }
}
