package wang.liangchen.matrix.framework.ddd.domain.entity;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.IIdentity;

/**
 * 实体基类（纯标记基类）：实体以唯一身份标识定义相等性（《解构领域驱动设计》）。
 * 基类不持有身份标识——不同实体的标识属性命名各异（如orderId、userId），由实体自行声明并以@Identity注解标注；
 * 实体自行按身份标识类型与值实现equals/hashCode（可借助AbstractIdentity的值相等语义）。
 * 建议实体的身份标识类型在业务上唯一（如OrderId、UserId），以避免不同类型实体之间的歧义相等。
 * 所有状态变更应通过明确的业务方法完成，不暴露公共setter。
 *
 * @author Liangchen.Wang
 */
@DomainModel(DomainMetaModel.Entity)
public abstract class AbstractEntity<ID extends IIdentity> implements IEntity<ID> {

}
