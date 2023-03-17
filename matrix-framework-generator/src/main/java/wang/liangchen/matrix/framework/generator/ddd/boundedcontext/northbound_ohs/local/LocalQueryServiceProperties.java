package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.local;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.EntityProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.ManagerProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl.QueryRequestMessagePlProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl.QueryResponseMessagePlProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class LocalQueryServiceProperties extends LocalProperties {
    private String queryServicePackage;
    private String queryServiceClassName;

    private EntityProperties entityProperties;
    private ManagerProperties managerProperties;
    private QueryRequestMessagePlProperties queryRequestMessagePlProperties;
    private QueryResponseMessagePlProperties queryResponseMessagePlProperties;

    public String getQueryServicePackage() {
        return queryServicePackage;
    }

    public void setQueryServicePackage(String queryServicePackage) {
        this.queryServicePackage = queryServicePackage;
    }

    public String getQueryServiceClassName() {
        return queryServiceClassName;
    }

    public void setQueryServiceClassName(String queryServiceClassName) {
        this.queryServiceClassName = queryServiceClassName;
    }

    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
    }

    public ManagerProperties getManagerProperties() {
        return managerProperties;
    }

    public void setManagerProperties(ManagerProperties managerProperties) {
        this.managerProperties = managerProperties;
    }

    public QueryRequestMessagePlProperties getQueryRequestMessagePlProperties() {
        return queryRequestMessagePlProperties;
    }

    public void setQueryRequestMessagePlProperties(QueryRequestMessagePlProperties queryRequestMessagePlProperties) {
        this.queryRequestMessagePlProperties = queryRequestMessagePlProperties;
    }

    public QueryResponseMessagePlProperties getQueryResponseMessagePlProperties() {
        return queryResponseMessagePlProperties;
    }

    public void setQueryResponseMessagePlProperties(QueryResponseMessagePlProperties queryResponseMessagePlProperties) {
        this.queryResponseMessagePlProperties = queryResponseMessagePlProperties;
    }
}
