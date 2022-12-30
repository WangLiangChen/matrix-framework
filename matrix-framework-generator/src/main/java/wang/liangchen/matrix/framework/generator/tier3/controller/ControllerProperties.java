package wang.liangchen.matrix.framework.generator.tier3.controller;

import wang.liangchen.matrix.framework.generator.tier3.EntitiesProperties;

/**
 * @author Liangchen.Wang 2022-12-30 11:22
 */
public class ControllerProperties extends EntitiesProperties {
    private String controllerName;
    private String controllerPackage;

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }
}
