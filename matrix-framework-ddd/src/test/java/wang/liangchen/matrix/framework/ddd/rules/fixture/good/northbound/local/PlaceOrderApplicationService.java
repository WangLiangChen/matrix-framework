package wang.liangchen.matrix.framework.ddd.rules.fixture.good.northbound.local;

import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.ICommandApplicationService;

/** 合规命令应用服务 */
@ApplicationService(ApplicationServiceType.COMMAND)
public final class PlaceOrderApplicationService implements ICommandApplicationService {
}
