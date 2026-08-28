package wang.liangchen.matrix.shop.product.domain.product;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/**
 * 商品已创建：商品进入商品目录的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class ProductCreated extends AbstractDomainEvent {

    private final ProductId productId;
    private final String productName;

    public ProductCreated(ProductId productId, String productName) {
        super();
        this.productId = productId;
        this.productName = productName;
    }

    public ProductId productId() {
        return productId;
    }

    public String productName() {
        return productName;
    }
}
