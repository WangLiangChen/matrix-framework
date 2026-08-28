package wang.liangchen.matrix.shop.product.message.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.event.AbstractContractEvent;

import java.time.Instant;

/**
 * 商品已下架事件契约：商品上下文对外发布的"下架已发生"事实（发布语言），
 * 仅携带消费方必要的数据（基本类型值），不依赖领域模型；
 * 由装配器翻译领域事件时复制事件标识值与发生时间。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.EVENT, exchangePattern = MessageExchangePattern.RequestStream)
public final class ProductDelistedEvent extends AbstractContractEvent {

    private final String productId;

    @JsonCreator
    public ProductDelistedEvent(@JsonProperty("eventId") String eventId,
                                 @JsonProperty("occurredOn") Instant occurredOn,
                                 @JsonProperty("productId") String productId) {
        super(eventId, occurredOn);
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }
}
