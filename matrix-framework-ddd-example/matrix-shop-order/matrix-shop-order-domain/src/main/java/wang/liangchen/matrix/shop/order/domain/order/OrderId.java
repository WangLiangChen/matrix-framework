package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.IStringIdentity;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;

import java.util.UUID;

/**
 * 订单身份标识：系统生成的代理标识，无业务含义。
 */
@DomainModel(DomainMetaModel.Identity)
public record OrderId(String value) implements IStringIdentity {

    public OrderId {
        if (value == null || value.isBlank()) {
            throw new DomainException("订单标识不能为空");
        }
    }

    public static OrderId of(String value) {
        return new OrderId(value);
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}