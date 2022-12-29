package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.controller;

import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.RemoteProperties;

/**
 * @author Liangchen.Wang 2022-12-29 8:05
 */
public class RemoteControllerProperties extends RemoteProperties{
    private String remoteControllerPackage;

    public String getRemoteControllerPackage() {
        return remoteControllerPackage;
    }

    public void setRemoteControllerPackage(String remoteControllerPackage) {
        this.remoteControllerPackage = remoteControllerPackage;
    }
}
