package wang.liangchen.matrix.shop.order.domain.shared;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 金额：表达订单交易中的商品价格与订单总额，
 * 为订单上下文的值对象（与商品上下文的Money语义分离），
 * 由订单与购物车两个聚合共享，置于共享内核包。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record Money(BigDecimal amount, String currency) implements IValueObject {

    public static final Money ZERO = new Money(BigDecimal.ZERO, "CNY");

    public Money {
        Objects.requireNonNull(amount, "金额数额不能为空");
        Objects.requireNonNull(currency, "金额币种不能为空");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("金额不能为负数");
        }
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money CNY(BigDecimal amount) {
        return new Money(amount, "CNY");
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public Money multiply(int multiplier) {
        if (multiplier < 0) {
            throw new DomainException("金额倍数不能为负数");
        }
        return new Money(amount.multiply(BigDecimal.valueOf(multiplier)), currency);
    }

    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new DomainException("不同币种的金额不能相加");
        }
        return new Money(amount.add(other.amount), currency);
    }
}
