package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.subscriber;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.RemoteProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class RemoteSubscriberProperties extends RemoteProperties {
    private String remoteSubscriberPackage;

    public void setRemoteSubscriberPackage(String remoteSubscriberPackage) {
        this.remoteSubscriberPackage = remoteSubscriberPackage;
    }

    public String getRemoteSubscriberPackage() {
        return remoteSubscriberPackage;
    }
}
