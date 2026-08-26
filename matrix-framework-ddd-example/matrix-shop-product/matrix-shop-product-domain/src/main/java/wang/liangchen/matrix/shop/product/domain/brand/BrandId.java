package wang.liangchen.matrix.shop.product.domain.brand;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.AbstractIdentity;
import wang.liangchen.matrix.framework.ddd.domain.identity.IStringIdentity;

import java.util.Objects;
import java.util.UUID;

/**
 * 品牌身份标识：系统生成的代理标识，无业务含义。
 */
@DomainModel(DomainMetaModel.Identity)
public final class BrandId extends AbstractIdentity implements IStringIdentity {

    private final String value;

    private BrandId(String value) {
        this.value = Objects.requireNonNull(value, "品牌标识不能为空");
    }

    public static BrandId of(String value) {
        return new BrandId(value);
    }

    public static BrandId generate() {
        return new BrandId(UUID.randomUUID().toString());
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
