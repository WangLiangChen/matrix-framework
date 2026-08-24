package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.domain;

import wang.liangchen.matrix.framework.ddd.contract.event.AbstractContractEvent;

/** 违规：领域类继承框架契约事件基类AbstractContractEvent（框架包依赖盲区用例） */
public final class DomainDependsOnFrameworkContract extends AbstractContractEvent {
}
