package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port.client;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port.PortProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class PortClientProperties extends PortProperties {
    private String portClientPackage;
    private String portClientName;

    public void setPortClientPackage(String portClientPackage) {
        this.portClientPackage = portClientPackage;
    }

    public String getPortClientPackage() {
        return portClientPackage;
    }

    public void setPortClientName(String portClientName) {
        this.portClientName = portClientName;
    }

    public String getPortClientName() {
        return portClientName;
    }
}
