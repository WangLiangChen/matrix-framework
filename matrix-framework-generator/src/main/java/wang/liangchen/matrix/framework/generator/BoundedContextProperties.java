package wang.liangchen.matrix.framework.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-12-28 19:07
 */
public class BoundedContextProperties {
    private String boundedContextPackage;
    private String boundedContextName;
    private String basePackage;
    private String datasource;
    private String author;
    private String output;
    private DomainProperties domainProperties;
    private NorthboundOhsProperties northboundOhsProperties;
    private SouthboundAclProperties southboundAclProperties;
    private List<AggregateProperties> aggregatesProperties = new ArrayList<>();

    public String getBoundedContextPackage() {
        return boundedContextPackage;
    }

    public void setBoundedContextPackage(String boundedContextPackage) {
        this.boundedContextPackage = boundedContextPackage;
    }

    public String getBoundedContextName() {
        return boundedContextName;
    }

    public void setBoundedContextName(String boundedContextName) {
        this.boundedContextName = boundedContextName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public List<AggregateProperties> getAggregatesProperties() {
        return aggregatesProperties;
    }

    public DomainProperties getDomainProperties() {
        return domainProperties;
    }

    public void setDomainProperties(DomainProperties domainProperties) {
        this.domainProperties = domainProperties;
    }

    public NorthboundOhsProperties getNorthboundOhsProperties() {
        return northboundOhsProperties;
    }

    public void setNorthboundOhsProperties(NorthboundOhsProperties northboundOhsProperties) {
        this.northboundOhsProperties = northboundOhsProperties;
    }

    public SouthboundAclProperties getSouthboundAclProperties() {
        return southboundAclProperties;
    }

    public void setSouthboundAclProperties(SouthboundAclProperties southboundAclProperties) {
        this.southboundAclProperties = southboundAclProperties;
    }

    public void setAggregatesProperties(List<AggregateProperties> aggregatesProperties) {
        this.aggregatesProperties = aggregatesProperties;
    }

    public void addAggregateProperties(AggregateProperties aggregateProperties) {
        this.aggregatesProperties.add(aggregateProperties);
    }
}
