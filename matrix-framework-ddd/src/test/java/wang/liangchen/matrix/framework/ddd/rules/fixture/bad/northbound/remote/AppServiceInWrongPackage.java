package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.northbound.remote;

import wang.liangchen.matrix.framework.ddd.northbound.local.IApplicationService;

/** 违规：应用服务实现类位于northbound.remote包（放置规则：应在northbound.local） */
public final class AppServiceInWrongPackage implements IApplicationService {
}
