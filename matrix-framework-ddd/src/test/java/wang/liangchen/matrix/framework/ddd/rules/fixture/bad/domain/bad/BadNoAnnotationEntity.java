package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.entity.IEntity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.framework.ddd.domain.identity.UUIDIdentity;

/** 违规：实现IEntity但未标注@DomainModel */
public final class BadNoAnnotationEntity implements IEntity<UUIDIdentity> {

    @Identity
    private final UUIDIdentity id;

    public BadNoAnnotationEntity(UUIDIdentity id) {
        this.id = id;
    }
}
