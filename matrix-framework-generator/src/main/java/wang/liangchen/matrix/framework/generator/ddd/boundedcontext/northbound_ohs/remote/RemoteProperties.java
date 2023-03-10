package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote;

import wang.liangchen.matrix.framework.ddd.northbound_ohs.remote.RemoteType;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.NorthboundOhsProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class RemoteProperties extends NorthboundOhsProperties{
    private String remotePackage;
    private String remoteClassName;
    private RemoteType remoteType;

    public void setRemotePackage(String remotePackage) {
        this.remotePackage = remotePackage;
    }

    public String getRemotePackage() {
        return remotePackage;
    }

    public String getRemoteClassName() {
        return remoteClassName;
    }

    public void setRemoteClassName(String remoteClassName) {
        this.remoteClassName = remoteClassName;
    }

    public RemoteType getRemoteType() {
        return remoteType;
    }

    public void setRemoteType(RemoteType remoteType) {
        this.remoteType = remoteType;
    }
}
