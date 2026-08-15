package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.northbound.remote;

import wang.liangchen.matrix.framework.ddd.northbound.remote.IProviderRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;

/** 违规：实现IProviderRemote但标注@Remote(Resource) */
@Remote(RemoteType.Resource)
public final class BadRemoteWrongType implements IProviderRemote {
}
