package wang.liangchen.matrix.framework.ddd.contract;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * MessageContract And PublishLanguage
 * 标识消息契约
 * 为了保护领域模型，防腐层和开放主机服务操作的对象都不应该是各自的领域模型
 * 操作类型（MessageContractType）：COMMAND_REQUEST、QUERY_REQUEST、EVENT、SCHEDULING，另有通用类型REQUEST/RESPONSE/RESULT/VIEW
 * 协作模式（MessageExchangePattern）：请求响应(RequestResponse)、即发即忘(FireAndForget)、发布/订阅(RequestStream)、消息通道(RequestChannel)
 * 交换方式按操作类型约定：查询用RequestResponse，命令用FireAndForget，事件用RequestStream（发布/订阅），调度用FireAndForget或RequestResponse
 * 操作类型+协作模式确定消息契约模型
 * 请求命名：动名词 + QueryRequest、动名词 + CommandRequest
 * 响应命名：查询响应(Response)、命令响应(Result)、视图(View)
 * direction/type/exchangePattern 必填（无默认值），强制契约作者显式声明方向、类型与交换方式
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MessageContract {
    MessageDirection direction();

    MessageContractType type();

    /**
     * 交换方式：查询用RequestResponse，命令用FireAndForget（必要时可请求确认），事件用RequestStream（发布/订阅）。
     */
    MessageExchangePattern exchangePattern();
}
