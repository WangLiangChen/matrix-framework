package wang.liangchen.matrix.framework.ddd.southbound_acl;

/**
 * @author Liangchen.Wang
 */
public enum PortType {
    //
    Repository("隔离访问数据库"),
    Client("隔离访问上游限界上下文或第三方服务"),
    Publisher("隔离对事件总线的访问"),
    File("隔离文件访问"),
    OSS("隔离对象存储服务访问");
    private final String summary;


    PortType(String summary) {
        this.summary = summary;
    }
}
