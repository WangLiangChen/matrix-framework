package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.SouthboundAclProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class AdapterProperties extends SouthboundAclProperties{
    private String adapterPackage;

    public void setAdapterPackage(String adapterPackage) {
        this.adapterPackage = adapterPackage;
    }

    public String getAdapterPackage() {
        return adapterPackage;
    }
}
