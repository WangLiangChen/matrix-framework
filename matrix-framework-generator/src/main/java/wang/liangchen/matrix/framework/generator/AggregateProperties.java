package wang.liangchen.matrix.framework.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-12-28 19:05
 */
public class AggregateProperties extends DomainProperties{
    private String aggregatePackage;
    private String aggregateName;
    private List<EntityProperties> entitiesProperties = new ArrayList<>();

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

    public List<EntityProperties> getEntitiesProperties() {
        return entitiesProperties;
    }

    public void setEntitiesProperties(List<EntityProperties> entitiesProperties) {
        this.entitiesProperties = entitiesProperties;
    }

    public void addEntityProperties(EntityProperties entityProperties) {
        this.entitiesProperties.add(entityProperties);
    }
}
