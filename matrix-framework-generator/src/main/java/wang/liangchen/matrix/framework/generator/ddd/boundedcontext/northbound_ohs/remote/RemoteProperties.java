package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.NorthboundOhsProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class RemoteProperties extends NorthboundOhsProperties{
    private String remotePackage;

    public void setRemotePackage(String remotePackage) {
        this.remotePackage = remotePackage;
    }

    public String getRemotePackage() {
        return remotePackage;
    }
}
