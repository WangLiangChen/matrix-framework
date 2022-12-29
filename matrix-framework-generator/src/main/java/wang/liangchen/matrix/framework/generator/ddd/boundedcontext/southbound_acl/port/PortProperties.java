package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.SouthboundAclProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class PortProperties extends SouthboundAclProperties {
    private String portPackage;

    public void setPortPackage(String portPackage) {
        this.portPackage = portPackage;
    }

    public String getPortPackage() {
        return portPackage;
    }
}
