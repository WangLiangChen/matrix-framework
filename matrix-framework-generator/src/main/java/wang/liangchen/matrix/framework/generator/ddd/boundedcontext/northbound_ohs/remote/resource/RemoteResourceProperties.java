package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.resource;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.RemoteProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class RemoteResourceProperties extends RemoteProperties{
    private String remoteResourcePackage;

    public void setRemoteResourcePackage(String remoteResourcePackage) {
        this.remoteResourcePackage = remoteResourcePackage;
    }

    public String getRemoteResourcePackage() {
        return remoteResourcePackage;
    }
}
