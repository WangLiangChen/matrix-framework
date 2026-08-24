package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.northbound.remote;

import wang.liangchen.matrix.framework.ddd.domain.identity.IIdentity;

/** 违规：远程服务直接依赖框架领域类型IIdentity（框架包依赖盲区用例） */
public final class RemoteDependsOnFrameworkDomain {

    private final IIdentity identity;

    public RemoteDependsOnFrameworkDomain(IIdentity identity) {
        this.identity = identity;
    }
}
