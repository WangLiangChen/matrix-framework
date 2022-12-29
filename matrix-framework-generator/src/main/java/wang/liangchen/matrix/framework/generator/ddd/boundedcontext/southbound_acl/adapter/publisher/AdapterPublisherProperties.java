package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter.publisher;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter.AdapterProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class AdapterPublisherProperties extends AdapterProperties{
    private String adapterPublisherPackage;
    private String adapterPublisherName;
    private String portPublisherPackage;
    private String portPublisherName;

    public void setAdapterPublisherPackage(String adapterPublisherPackage) {
        this.adapterPublisherPackage = adapterPublisherPackage;
    }

    public String getAdapterPublisherPackage() {
        return adapterPublisherPackage;
    }

    public void setAdapterPublisherName(String adapterPublisherName) {
        this.adapterPublisherName = adapterPublisherName;
    }

    public String getAdapterPublisherName() {
        return adapterPublisherName;
    }

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
