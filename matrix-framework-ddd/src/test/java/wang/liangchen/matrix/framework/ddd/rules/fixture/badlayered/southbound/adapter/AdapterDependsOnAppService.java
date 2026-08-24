package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.southbound.adapter;

import wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.northbound.local.SomeAppService;

/** 违规：南向适配器反向依赖北向应用服务（依赖方向只能由外向内） */
public final class AdapterDependsOnAppService {

    @SuppressWarnings("unused")
    private final SomeAppService appService;

    public AdapterDependsOnAppService(SomeAppService appService) {
        this.appService = appService;
    }
}
