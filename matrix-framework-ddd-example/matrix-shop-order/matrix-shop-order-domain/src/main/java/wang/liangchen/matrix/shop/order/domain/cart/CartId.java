package wang.liangchen.matrix.shop.order.domain.cart;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.IStringIdentity;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;

import java.util.UUID;

/**
 * 购物车身份标识：系统生成的代理标识，无业务含义。
 */
@DomainModel(DomainMetaModel.Identity)
public record CartId(String value) implements IStringIdentity {

    public CartId {
        if (value == null || value.isBlank()) {
            throw new DomainException("购物车标识不能为空");
        }
    }

    public static CartId of(String value) {
        return new CartId(value);
    }

    public static CartId generate() {
        return new CartId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}