package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.EntityProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class QueryRequestMessagePlProperties extends NorthboundMessagePlProperties {
    private String queryRequestPackage;
    private String queryRequestClassName;

    private EntityProperties entityProperties;

    public String getQueryRequestPackage() {
        return queryRequestPackage;
    }

    public void setQueryRequestPackage(String queryRequestPackage) {
        this.queryRequestPackage = queryRequestPackage;
    }

    public String getQueryRequestClassName() {
        return queryRequestClassName;
    }

    public void setQueryRequestClassName(String queryRequestClassName) {
        this.queryRequestClassName = queryRequestClassName;
    }

    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
    }
}
