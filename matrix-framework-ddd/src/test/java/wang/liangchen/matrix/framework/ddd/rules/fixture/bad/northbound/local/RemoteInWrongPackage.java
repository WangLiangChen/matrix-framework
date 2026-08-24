package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.northbound.local;

import wang.liangchen.matrix.framework.ddd.northbound.remote.IRemote;

/** 违规：远程服务实现类位于northbound.local包（放置规则：应在northbound.remote） */
public final class RemoteInWrongPackage implements IRemote {
}
