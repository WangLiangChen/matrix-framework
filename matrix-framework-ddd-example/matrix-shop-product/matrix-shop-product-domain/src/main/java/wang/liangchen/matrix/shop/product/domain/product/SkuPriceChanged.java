package wang.liangchen.matrix.shop.product.domain.product;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/**
 * SKU价格已变更：SKU销售价格发生调整的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class SkuPriceChanged extends AbstractDomainEvent {

    private final ProductId productId;
    private final SkuId skuId;
    private final Money price;

    public SkuPriceChanged(ProductId productId, SkuId skuId, Money price) {
        super();
        this.productId = productId;
        this.skuId = skuId;
        this.price = price;
    }

    public ProductId productId() {
        return productId;
    }

    public SkuId skuId() {
        return skuId;
    }

    public Money price() {
        return price;
    }
}
