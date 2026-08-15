package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.northbound.local;

import wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.southbound.adapter.SomeAdapter;

/** 违规：应用服务依赖南向适配器实现（应通过端口接口访问外部资源） */
public final class AppServiceDependsOnAdapter {

    @SuppressWarnings("unused")
    private final SomeAdapter adapter;

    public AppServiceDependsOnAdapter(SomeAdapter adapter) {
        this.adapter = adapter;
    }
}
