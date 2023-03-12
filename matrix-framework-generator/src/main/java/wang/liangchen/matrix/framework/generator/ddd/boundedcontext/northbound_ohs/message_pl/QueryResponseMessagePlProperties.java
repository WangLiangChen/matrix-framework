package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.EntityProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class QueryResponseMessagePlProperties extends NorthboundMessagePlProperties {
    private String queryResponsePackage;
    private String queryResponseClassName;
    private EntityProperties entityProperties;

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

    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
    }
}
