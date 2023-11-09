package wang.liangchen.matrix.framework.ddd.message_pl;

/**
 * @author Liangchen.Wang
 * Marker annotation
 * MessageContract And PublishLanguage
 * 标识消息契约
 * 为了保护领域模型，防腐层和开放主机服务操作的对象都不应该是各自的领域模型
 * 操作类型：COMANND、QUERY、EVENT、SCHEDULING
 * 协作模式：请求响应(request/response)、即发即忘(fire-and-forget)、发布/订阅(publish/subscribe)、定时调度(timing-trigge)
 * 操作类型+协作模式确定消息契约模型
 * 请求命名：动名词 + QueryRequest、动名词 + CommandRequest
 * 响应命名：查询响应(Response)、命令响应(Result)、视图(Presentation/View)
 */
public @interface MessageContract {
    Direction direction() default Direction.NONE;

    MessageContractType type() default MessageContractType.NONE;
}
