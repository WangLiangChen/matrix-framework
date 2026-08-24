package wang.liangchen.matrix.framework.ddd.rules.fixture.good.northbound.remote;

import wang.liangchen.matrix.framework.ddd.northbound.remote.IControllerRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;

/** 合规面向UI的控制器远程服务 */
@Remote(RemoteType.Controller)
public final class OrderController implements IControllerRemote {
}
