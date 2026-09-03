package wang.liangchen.matrix.framework.ddd.domain.identity;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.AbstractValueObject;

/**
 * 简单身份标识基类：包装单个值(T)的身份标识，是值对象的特例。
 * 相等性与哈希继承自AbstractValueObject（按字段值比较），子类应为final并以静态工厂of创建。
 * 多字段/复合身份标识可改为继承AbstractIdentity。
 *
 * @author Liangchen.Wang
 */
@DomainModel(DomainMetaModel.Identity)
public abstract class AbstractSimpleIdentity<T> extends AbstractValueObject implements ISimpleIdentity<T> {
}
