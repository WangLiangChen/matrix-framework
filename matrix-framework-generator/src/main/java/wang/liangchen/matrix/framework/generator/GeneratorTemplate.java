package wang.liangchen.matrix.framework.generator;

import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-04-28 9:51
 */
public class GeneratorTemplate extends GeneratorProperties {
    private String domainPackage;
    private String southboundAclPackage;
    private String portPackage;
    private String adapterPackage;
    private String repositoryPackage;
    private String clientPackage;
    private String publisherPackage;

    private String northboundOhsPackage;
    private String localPackage;
    private String remotePackage;
    private String controllerPackage;
    private String providerPackage;
    private String resourcePackage;
    private String subscriberPackage;
    private String messageContractPublishLanguagePackage;
    private String northPackage;
    private String southPackage;
    private Set<String> imports = new HashSet<>();
    private List<ColumnMeta> columnMetas = new ArrayList<>();


    public String getDomainPackage() {
        return domainPackage;
    }

    public void setDomainPackage(String domainPackage) {
        this.domainPackage = domainPackage;
    }

    public Set<String> getImports() {
        return imports;
    }

    public List<ColumnMeta> getColumnMetas() {
        return columnMetas;
    }

    public void setSouthboundAclPackage(String southboundAclPackage) {
        this.southboundAclPackage = southboundAclPackage;
    }

    public String getSouthboundAclPackage() {
        return southboundAclPackage;
    }

    public void setPortPackage(String portPackage) {
        this.portPackage = portPackage;
    }

    public String getPortPackage() {
        return portPackage;
    }

    public void setAdapterPackage(String adapterPackage) {
        this.adapterPackage = adapterPackage;
    }

    public String getAdapterPackage() {
        return adapterPackage;
    }

    public void setRepositoryPackage(String repositoryPackage) {
        this.repositoryPackage = repositoryPackage;
    }

    public String getRepositoryPackage() {
        return repositoryPackage;
    }

    public void setClientPackage(String clientPackage) {
        this.clientPackage = clientPackage;
    }

    public String getClientPackage() {
        return clientPackage;
    }

    public void setPublisherPackage(String publisherPackage) {
        this.publisherPackage = publisherPackage;
    }

    public String getPublisherPackage() {
        return publisherPackage;
    }

    public void setNorthboundOhsPackage(String northboundOhsPackage) {
        this.northboundOhsPackage = northboundOhsPackage;
    }

    public String getNorthboundOhsPackage() {
        return northboundOhsPackage;
    }

    public void setLocalPackage(String localPackage) {
        this.localPackage = localPackage;
    }

    public String getLocalPackage() {
        return localPackage;
    }

    public void setRemotePackage(String remotePackage) {
        this.remotePackage = remotePackage;
    }

    public String getRemotePackage() {
        return remotePackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public void setProviderPackage(String providerPackage) {
        this.providerPackage = providerPackage;
    }

    public String getProviderPackage() {
        return providerPackage;
    }

    public void setResourcePackage(String resourcePackage) {
        this.resourcePackage = resourcePackage;
    }

    public String getResourcePackage() {
        return resourcePackage;
    }

    public void setSubscriberPackage(String subscriberPackage) {
        this.subscriberPackage = subscriberPackage;
    }

    public String getSubscriberPackage() {
        return subscriberPackage;
    }

    public void setMessageContractPublishLanguagePackage(String messageContractPublishLanguagePackage) {
        this.messageContractPublishLanguagePackage = messageContractPublishLanguagePackage;
    }

    public String getMessageContractPublishLanguagePackage() {
        return messageContractPublishLanguagePackage;
    }

    public String getNorthPackage() {
        return northPackage;
    }

    public void setNorthPackage(String northPackage) {
        this.northPackage = northPackage;
    }

    public String getSouthPackage() {
        return southPackage;
    }

    public void setSouthPackage(String southPackage) {
        this.southPackage = southPackage;
    }
}
