package wang.liangchen.matrix.framework.generator.tier3.service;

import wang.liangchen.matrix.framework.generator.tier3.controller.ControllerProperties;

/**
 * @author Liangchen.Wang 2022-12-30 11:21
 */
public class ServiceProperties extends ControllerProperties {
    private String serviceName;
    private String servicePackage;
    private String serviceImplName;
    private String serviceImplPackage;
    private String requestName;
    private String requestPackage;
    private String responseName;
    private String responsePackage;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServicePackage() {
        return servicePackage;
    }

    public void setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
    }

    public String getServiceImplName() {
        return serviceImplName;
    }

    public void setServiceImplName(String serviceImplName) {
        this.serviceImplName = serviceImplName;
    }

    public String getServiceImplPackage() {
        return serviceImplPackage;
    }

    public void setServiceImplPackage(String serviceImplPackage) {
        this.serviceImplPackage = serviceImplPackage;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestPackage() {
        return requestPackage;
    }

    public void setRequestPackage(String requestPackage) {
        this.requestPackage = requestPackage;
    }

    public String getResponseName() {
        return responseName;
    }

    public void setResponseName(String responseName) {
        this.responseName = responseName;
    }

    public String getResponsePackage() {
        return responsePackage;
    }

    public void setResponsePackage(String responsePackage) {
        this.responsePackage = responsePackage;
    }
}
