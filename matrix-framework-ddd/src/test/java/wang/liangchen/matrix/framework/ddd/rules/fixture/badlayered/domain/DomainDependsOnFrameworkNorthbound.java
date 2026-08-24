package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.domain;

import wang.liangchen.matrix.framework.ddd.northbound.local.ICommandApplicationService;

/** 违规：领域类实现框架北向接口ICommandApplicationService（框架包依赖盲区用例） */
public final class DomainDependsOnFrameworkNorthbound implements ICommandApplicationService {
}
