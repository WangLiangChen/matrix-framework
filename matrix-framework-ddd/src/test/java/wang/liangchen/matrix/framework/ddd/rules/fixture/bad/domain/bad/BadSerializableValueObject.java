package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.AbstractValueObject;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;

import java.io.Serializable;

/** 违规：领域对象实现Serializable（不可序列化直传） */
@DomainModel(DomainMetaModel.ValueObject)
public final class BadSerializableValueObject extends AbstractValueObject implements IValueObject, Serializable {

    private final String value;

    public BadSerializableValueObject(String value) {
        this.value = value;
    }
}
