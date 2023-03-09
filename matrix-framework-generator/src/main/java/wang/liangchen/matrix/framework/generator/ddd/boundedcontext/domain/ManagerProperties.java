package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.BoundedContextProperties;


/**
 * @author Liangchen.Wang 2023-03-09 8:45
 */
public class ManagerProperties extends BoundedContextProperties {
    private String managerPackage;
    private String managerClassName;
    private EntityProperties entityProperties;

    public String getManagerPackage() {
        return managerPackage;
    }

    public void setManagerPackage(String managerPackage) {
        this.managerPackage = managerPackage;
    }

    public String getManagerClassName() {
        return managerClassName;
    }

    public void setManagerClassName(String managerClassName) {
        this.managerClassName = managerClassName;
    }

    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
    }


}
