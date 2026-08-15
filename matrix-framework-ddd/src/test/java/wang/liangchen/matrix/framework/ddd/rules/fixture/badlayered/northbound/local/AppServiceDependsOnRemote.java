package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.northbound.local;

import wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.northbound.remote.SomeRemote;

/** 违规：应用服务依赖远程层（远程服务通过应用服务完成用例编排） */
public final class AppServiceDependsOnRemote {

    @SuppressWarnings("unused")
    private final SomeRemote remote;

    public AppServiceDependsOnRemote(SomeRemote remote) {
        this.remote = remote;
    }
}
