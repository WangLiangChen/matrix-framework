package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class QueryResponseMessagePlProperties extends NorthboundMessagePlProperties {
    private String queryResponsePackage;
    private String queryResponseClassName;

    public String getQueryResponsePackage() {
        return queryResponsePackage;
    }

    public void setQueryResponsePackage(String queryResponsePackage) {
        this.queryResponsePackage = queryResponsePackage;
    }

    public String getQueryResponseClassName() {
        return queryResponseClassName;
    }

    public void setQueryResponseClassName(String queryResponseClassName) {
        this.queryResponseClassName = queryResponseClassName;
    }
}
