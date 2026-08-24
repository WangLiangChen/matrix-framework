package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.message;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;

/** 违规：值对象实现类位于message包（放置规则：应在domain包，message包只放契约） */
@DomainModel(DomainMetaModel.ValueObject)
public final class ValueObjectInWrongPackage implements IValueObject {
}
