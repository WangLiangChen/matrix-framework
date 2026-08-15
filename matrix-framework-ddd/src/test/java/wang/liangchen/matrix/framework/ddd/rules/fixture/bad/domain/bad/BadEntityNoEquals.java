package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.entity.IEntity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.framework.ddd.domain.identity.UUIDIdentity;

/** 违规：实体未重写equals/hashCode（相等性必须按身份标识定义） */
@DomainModel(DomainMetaModel.Entity)
public final class BadEntityNoEquals implements IEntity<UUIDIdentity> {

    @Identity
    private final UUIDIdentity id;

    public BadEntityNoEquals(UUIDIdentity id) {
        this.id = id;
    }
}
