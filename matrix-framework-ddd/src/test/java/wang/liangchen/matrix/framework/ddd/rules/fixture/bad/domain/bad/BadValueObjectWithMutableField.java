package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.AbstractValueObject;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;

/** 违规：值对象实例字段未声明final（不可变） */
@DomainModel(DomainMetaModel.ValueObject)
public final class BadValueObjectWithMutableField extends AbstractValueObject implements IValueObject {

    private String value;

    public BadValueObjectWithMutableField(String value) {
        this.value = value;
    }
}
