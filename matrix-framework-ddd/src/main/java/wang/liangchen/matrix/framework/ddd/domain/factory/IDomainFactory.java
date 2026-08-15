package wang.liangchen.matrix.framework.ddd.domain.factory;

/**
 * @author Liangchen.Wang
 * Marker interface
 * Mark a domain factory
 * 领域工厂封装复杂的聚合根、实体、值对象的创建逻辑；实现本接口的类必须自行标注
 * @DomainModel(DomainMetaModel.DomainFactory)（@DomainModel无@Inherited，标注不会沿继承传播）。
 */
public interface IDomainFactory {
}
