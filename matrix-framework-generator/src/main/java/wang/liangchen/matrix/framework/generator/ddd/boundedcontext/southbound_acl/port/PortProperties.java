package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port;

import wang.liangchen.matrix.framework.ddd.southbound_acl.port.PortType;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.EntityProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.SouthboundAclProperties;


/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class PortProperties extends SouthboundAclProperties {
    private String portPackage;
    private String portClassName;
    private PortType portType;
    private EntityProperties entityProperties;


    public void setPortPackage(String portPackage) {
        this.portPackage = portPackage;
    }

    public String getPortPackage() {
        return portPackage;
    }

    public String getPortClassName() {
        return portClassName;
    }

    public void setPortClassName(String portClassName) {
        this.portClassName = portClassName;
    }

    public PortType getPortType() {
        return portType;
    }

    public void setPortType(PortType portType) {
        this.portType = portType;
    }

    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
    }
}
