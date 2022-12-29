package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.provider;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.RemoteProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class RemoteProviderProperties extends RemoteProperties{
    private String remoteProviderPackage;

    public void setRemoteProviderPackage(String remoteProviderPackage) {
        this.remoteProviderPackage = remoteProviderPackage;
    }

    public String getRemoteProviderPackage() {
        return remoteProviderPackage;
    }
}
