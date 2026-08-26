package wang.liangchen.matrix.shop.product.domain.product;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.ISimpleIdentity;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

import java.util.UUID;

/**
 * 商品身份标识：系统生成的代理标识，无业务含义。
 */
@DomainModel(DomainMetaModel.Identity)
public record ProductId(String value) implements ISimpleIdentity<String> {

    public ProductId {
        if (value == null || value.isBlank()) {
            throw new DomainException("商品标识不能为空");
        }
    }

    public static ProductId of(String value) {
        return new ProductId(value);
    }

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
