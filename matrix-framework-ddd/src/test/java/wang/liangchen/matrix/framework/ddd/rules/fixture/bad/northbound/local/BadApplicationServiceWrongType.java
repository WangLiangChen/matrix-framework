package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.northbound.local;

import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.ICommandApplicationService;

/** 违规：实现ICommandApplicationService但标注@ApplicationService(QUERY) */
@ApplicationService(ApplicationServiceType.QUERY)
public final class BadApplicationServiceWrongType implements ICommandApplicationService {
}
