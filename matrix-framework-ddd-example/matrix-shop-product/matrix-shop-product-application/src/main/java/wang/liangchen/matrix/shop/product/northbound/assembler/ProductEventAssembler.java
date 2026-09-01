package wang.liangchen.matrix.shop.product.northbound.assembler;

import wang.liangchen.matrix.framework.ddd.assembler.AbstractAssembler;
import wang.liangchen.matrix.framework.ddd.assembler.Assembler;
import wang.liangchen.matrix.shop.product.domain.product.ProductDelistedEvent;

/**
 * 商品事件装配器：出站装配——将领域事件翻译为事件契约（发布语言），
 * 复制事件标识值与发生时间（幂等消费以eventId为准），只携带下游必要的数据。
 */
@Assembler
public class ProductEventAssembler extends AbstractAssembler {

    /**
     * 翻译"商品已下架"领域事件为事件契约。
     */
    public wang.liangchen.matrix.shop.product.message.event.ProductDelistedEvent toContractEvent(ProductDelistedEvent domainEvent) {
        return new wang.liangchen.matrix.shop.product.message.event.ProductDelistedEvent(domainEvent.getEventId(), domainEvent.getOccurredOn(),
                domainEvent.productId().value());
    }
}