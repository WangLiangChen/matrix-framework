package wang.liangchen.matrix.shop.product.domain.brand;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;

/**
 * 品牌已创建：品牌进入商品目录的领域事实。
 */
@DomainModel(DomainMetaModel.DomainEvent)
public final class BrandCreatedEvent extends AbstractDomainEvent {

    private final BrandId brandId;
    private final String brandName;

    public BrandCreatedEvent(BrandId brandId, String brandName) {
        super();
        this.brandId = brandId;
        this.brandName = brandName;
    }

    public BrandId brandId() {
        return brandId;
    }

    public String brandName() {
        return brandName;
    }
}
