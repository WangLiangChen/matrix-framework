package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.entity.IEntity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.framework.ddd.domain.identity.UUIDIdentity;

/** 违规：实体暴露公共setter，状态变更未通过业务方法 */
@DomainModel(DomainMetaModel.Entity)
public final class BadSetterEntity implements IEntity<UUIDIdentity> {

    @Identity
    private final UUIDIdentity id;

    private String name;

    public BadSetterEntity(UUIDIdentity id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
