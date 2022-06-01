package wang.liangchen.matrix.framework.ddd.southbound_acl;

/**
 * @author Liangchen.Wang
 */
public enum PortType {
    //
    Repository("访问数据库"),
    Client("访问上游限界上下文或第三方服务"),
    Publisher("发布事件消息"),
    File("访问文件"),
    OSS("对象存储服务");
    private final String summary;


    PortType(String summary) {
        this.summary = summary;
    }
}
