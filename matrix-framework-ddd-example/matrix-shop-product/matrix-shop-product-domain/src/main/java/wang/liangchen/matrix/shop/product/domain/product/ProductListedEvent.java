package wang.liangchen.matrix.shop.product.domain.product;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/**
 * 商品已上架：商品进入可售状态的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class ProductListedEvent extends AbstractDomainEvent {

    private final ProductId productId;

    public ProductListedEvent(ProductId productId) {
        super();
        this.productId = productId;
    }

    public ProductId productId() {
        return productId;
    }
}
