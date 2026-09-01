package wang.liangchen.matrix.shop.product.domain.attribute;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.IStringIdentity;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

import java.util.UUID;

/**
 * 属性身份标识：系统生成的代理标识，无业务含义。
 */
@DomainModel(DomainMetaModel.Identity)
public record AttributeId(String value) implements IStringIdentity {

    public AttributeId {
        if (value == null || value.isBlank()) {
            throw new DomainException("属性标识不能为空");
        }
    }

    public static AttributeId of(String value) {
        return new AttributeId(value);
    }

    public static AttributeId generate() {
        return new AttributeId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}