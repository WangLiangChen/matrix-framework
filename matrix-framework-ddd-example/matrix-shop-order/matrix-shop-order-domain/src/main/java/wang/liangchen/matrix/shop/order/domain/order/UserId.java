package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.IStringIdentity;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;

/**
 * 买家身份标识：订单上下文中对买家（外部用户实体）的身份引用。
 */
@DomainModel(DomainMetaModel.Identity)
public record UserId(String value) implements IStringIdentity {

    public UserId {
        if (value == null || value.isBlank()) {
            throw new DomainException("买家标识不能为空");
        }
    }

    public static UserId of(String value) {
        return new UserId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}