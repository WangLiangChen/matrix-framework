package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.northbound.remote;

import wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.southbound.adapter.SomeAdapter;

/** 违规：远程服务直接依赖南向适配器（外部资源访问须经应用服务与端口） */
public final class RemoteDependsOnAdapter {

    @SuppressWarnings("unused")
    private final SomeAdapter adapter;

    public RemoteDependsOnAdapter(SomeAdapter adapter) {
        this.adapter = adapter;
    }
}
