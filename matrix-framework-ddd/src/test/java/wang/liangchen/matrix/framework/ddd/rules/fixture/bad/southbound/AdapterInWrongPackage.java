package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.southbound;

import wang.liangchen.matrix.framework.ddd.southbound.adapter.IAdapter;

/** 违规：适配器实现类位于southbound根包（放置规则：应在southbound.adapter） */
public final class AdapterInWrongPackage implements IAdapter {
}
