package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port.repository;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port.PortProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class PortRepositoryProperties extends PortProperties {
    private String portRepositoryPackage;
    private String portRepositoryName;

    public void setPortRepositoryPackage(String portRepositoryPackage) {
        this.portRepositoryPackage = portRepositoryPackage;
    }

    public String getPortRepositoryPackage() {
        return portRepositoryPackage;
    }

    public String getPortRepositoryName() {
        return portRepositoryName;
    }

    public void setPortRepositoryName(String portRepositoryName) {
        this.portRepositoryName = portRepositoryName;
    }
}
