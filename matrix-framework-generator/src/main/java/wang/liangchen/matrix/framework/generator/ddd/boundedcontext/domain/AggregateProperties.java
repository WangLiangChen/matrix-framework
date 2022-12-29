package wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain;

/**
 * @author Liangchen.Wang 2022-12-28 19:05
 */
public class AggregateProperties extends DomainProperties {
    private String aggregatePackage;
    private String aggregateName;

    public String getAggregatePackage() {
        return aggregatePackage;
    }

    public void setAggregatePackage(String aggregatePackage) {
        this.aggregatePackage = aggregatePackage;
    }

    public String getAggregateName() {
        return aggregateName;
    }

    public void setAggregateName(String aggregateName) {
        this.aggregateName = aggregateName;
    }
}
