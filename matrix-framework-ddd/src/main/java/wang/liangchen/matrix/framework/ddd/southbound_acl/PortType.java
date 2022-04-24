package wang.liangchen.matrix.framework.ddd.southbound_acl;

/**
 * @author Liangchen.Wang
 */
public enum PortType {
    //
    Repository("调用数据库"),
    Client("调用上游或三方服务"),
    Publisher("发布事件");
    private final String summary;


    PortType(String summary) {
        this.summary = summary;
    }
}
