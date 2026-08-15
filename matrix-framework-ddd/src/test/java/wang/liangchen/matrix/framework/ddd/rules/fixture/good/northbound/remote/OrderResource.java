package wang.liangchen.matrix.framework.ddd.rules.fixture.good.northbound.remote;

import wang.liangchen.matrix.framework.ddd.northbound.remote.IResourceRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;

/** 合规远程服务 */
@Remote(RemoteType.Resource)
public final class OrderResource implements IResourceRemote {
}
