package wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.AbstractValueObject;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;

import java.math.BigDecimal;

/** 合规值对象（不可变） */
@DomainModel(DomainMetaModel.ValueObject)
public final class Money extends AbstractValueObject implements IValueObject {

    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }
}
