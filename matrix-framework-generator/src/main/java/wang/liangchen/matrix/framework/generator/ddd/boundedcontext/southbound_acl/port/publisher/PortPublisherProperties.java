package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port.publisher;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port.PortProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class PortPublisherProperties extends PortProperties {
    private String portPublisherPackage;
    private String portPublisherName;

    public void setPortPublisherPackage(String portPublisherPackage) {
        this.portPublisherPackage = portPublisherPackage;
    }

    public String getPortPublisherPackage() {
        return portPublisherPackage;
    }

    public void setPortPublisherName(String portPublisherName) {
        this.portPublisherName = portPublisherName;
    }

    public String getPortPublisherName() {
        return portPublisherName;
    }
}
