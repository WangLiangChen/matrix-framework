package wang.liangchen.matrix.framework.ddd.rules.fixture.good.northbound.local;

import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;

/** 合规查询应用服务 */
@ApplicationService(ApplicationServiceType.QUERY)
public final class OrderQueryApplicationService implements IQueryApplicationService {
}
