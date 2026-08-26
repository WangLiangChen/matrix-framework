package wang.liangchen.matrix.shop.order.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.ISimpleIdentity;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;

/**
 * 商品身份标识：订单上下文对商品聚合的身份引用。
 * 与商品上下文的ProductId语义分离（语义分歧），仅值相同，由防腐层在边界处翻译。
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

    @Override
    public String toString() {
        return value;
    }
}
