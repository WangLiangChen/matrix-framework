package wang.liangchen.matrix.shop.product.domain.product;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.IStringIdentity;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

import java.util.UUID;

/**
 * SKU身份标识：系统生成的代理标识，无业务含义。
 */
@DomainModel(DomainMetaModel.Identity)
public record SkuId(String value) implements IStringIdentity {

    public SkuId {
        if (value == null || value.isBlank()) {
            throw new DomainException("SKU标识不能为空");
        }
    }

    public static SkuId of(String value) {
        return new SkuId(value);
    }

    public static SkuId generate() {
        return new SkuId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}