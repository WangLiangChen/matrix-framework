package wang.liangchen.matrix.framework.ddd.domain.identity;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.AbstractValueObject;

/**
 * 身份标识基类：身份标识是值对象的特例——可为无业务含义的通用类型（如UUIDIdentity），
 * 也可为有业务含义的领域类型。
 *
 * @author Liangchen.Wang
 */
@DomainModel(DomainMetaModel.Identity)
public abstract class AbstractSimpleIdentity<T> extends AbstractValueObject implements ISimpleIdentity<T> {
}
