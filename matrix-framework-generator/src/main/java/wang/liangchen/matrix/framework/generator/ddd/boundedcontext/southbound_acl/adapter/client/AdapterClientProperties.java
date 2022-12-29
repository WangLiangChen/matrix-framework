package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter.client;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter.AdapterProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class AdapterClientProperties extends AdapterProperties {
    private String adapterClientPackage;
    private String adapterClientName;
    private String portClientPackage;
    private String portClientName;

    public void setAdapterClientPackage(String adapterClientPackage) {
        this.adapterClientPackage = adapterClientPackage;
    }

    public String getAdapterClientPackage() {
        return adapterClientPackage;
    }

    public void setAdapterClientName(String adapterClientName) {
        this.adapterClientName = adapterClientName;
    }

    public String getAdapterClientName() {
        return adapterClientName;
    }

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
