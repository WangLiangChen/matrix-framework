package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.domain;

import wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.southbound.adapter.SomeAdapter;

/** 违规：领域层依赖南向适配器实现（依赖倒置被破坏） */
public final class DomainDependsOnAdapter {

    @SuppressWarnings("unused")
    private final SomeAdapter adapter;

    public DomainDependsOnAdapter(SomeAdapter adapter) {
        this.adapter = adapter;
    }
}
