package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.EntityProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port.PortProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class AdapterProperties extends PortProperties {
    private String adapterPackage;
    private String adapterClassName;
    private EntityProperties entityProperties;

    public void setAdapterPackage(String adapterPackage) {
        this.adapterPackage = adapterPackage;
    }

    public String getAdapterPackage() {
        return adapterPackage;
    }

    public String getAdapterClassName() {
        return adapterClassName;
    }

    public void setAdapterClassName(String adapterClassName) {
        this.adapterClassName = adapterClassName;
    }

    @Override
    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    @Override
    public void setEntityProperties(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
    }
}
