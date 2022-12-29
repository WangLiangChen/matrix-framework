package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter.repository;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter.AdapterProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class AdapterRepositoryProperties extends AdapterProperties {

    private String portRepositoryPackage;
    private String portRepositoryName;
    private String adapterRepositoryPackage;
    private String adapterRepositoryName;

    public String getPortRepositoryPackage() {
        return portRepositoryPackage;
    }

    public void setPortRepositoryPackage(String portRepositoryPackage) {
        this.portRepositoryPackage = portRepositoryPackage;
    }

    public String getPortRepositoryName() {
        return portRepositoryName;
    }

    public void setPortRepositoryName(String portRepositoryName) {
        this.portRepositoryName = portRepositoryName;
    }

    public void setAdapterRepositoryPackage(String adapterRepositoryPackage) {
        this.adapterRepositoryPackage = adapterRepositoryPackage;
    }

    public String getAdapterRepositoryPackage() {
        return adapterRepositoryPackage;
    }

    public void setAdapterRepositoryName(String adapterRepositoryName) {
        this.adapterRepositoryName = adapterRepositoryName;
    }

    public String getAdapterRepositoryName() {
        return adapterRepositoryName;
    }
}
