package wang.liangchen.matrix.framework.ddd.northbound_ohs;

/**
 * @author Liangchen.Wang
 * 远程服务类型
 */
public enum RemoteType {
    /**
     *
     */
    Resource("基于资源,面向下游或第三方调用者,一般为Restful风格"),
    Controller("基于资源，面向UI"),
    Provider("基于行为,面向下游或第三方调用者"),
    Subscriber("基于事件，消息契约模型为事件");
    private final String summary;

    RemoteType(String summary) {
        this.summary = summary;
    }
}
