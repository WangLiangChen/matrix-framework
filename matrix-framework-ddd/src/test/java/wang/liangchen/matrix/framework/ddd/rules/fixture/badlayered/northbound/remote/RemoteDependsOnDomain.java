package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.northbound.remote;

import wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.domain.SomeDomainThing;

/** 违规：远程服务直接访问领域对象（远程服务只操作消息契约，经应用服务编排） */
public final class RemoteDependsOnDomain {

    @SuppressWarnings("unused")
    private final SomeDomainThing domain;

    public RemoteDependsOnDomain(SomeDomainThing domain) {
        this.domain = domain;
    }
}
