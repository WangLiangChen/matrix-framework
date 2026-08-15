package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.domain;

import wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.northbound.local.SomeAppService;

/** 违规：领域层依赖北向应用服务（依赖方向只能由外向内） */
public final class DomainDependsOnAppService {

    @SuppressWarnings("unused")
    private final SomeAppService appService;

    public DomainDependsOnAppService(SomeAppService appService) {
        this.appService = appService;
    }
}
