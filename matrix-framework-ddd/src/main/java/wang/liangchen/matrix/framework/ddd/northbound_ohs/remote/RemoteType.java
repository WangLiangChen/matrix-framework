package wang.liangchen.matrix.framework.ddd.northbound_ohs.remote;

/**
 * @author Liangchen.Wang
 * 远程服务类型
 */
public enum RemoteType {
    /**
     *
     */
    Resource("服务资源契约,面向下游限界上下文或第三方调用者,消息契约模型为Request/Response，一般为Restful"),
    Controller("服务资源契约，面向UI，消息契约模型为Presentation/View"),
    Provider("服务行为契约,面向下游限界上下文或第三方调用者,消息契约模型为Request/Response或FireAndForget,一般为RPC"),
    Subscriber("服务事件契约，消息契约模型为Event"),
    Scheduler("定时调度契约");
    private final String summary;

    RemoteType(String summary) {
        this.summary = summary;
    }
}
