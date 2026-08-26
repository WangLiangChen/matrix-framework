package wang.liangchen.matrix.shop.product.domain.category;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.ISimpleIdentity;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;

import java.util.UUID;

/**
 * 类目身份标识：系统生成的代理标识，无业务含义。
 */
@DomainModel(DomainMetaModel.Identity)
public record CategoryId(String value) implements ISimpleIdentity<String> {

    public CategoryId {
        if (value == null || value.isBlank()) {
            throw new DomainException("类目标识不能为空");
        }
    }

    public static CategoryId of(String value) {
        return new CategoryId(value);
    }

    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
