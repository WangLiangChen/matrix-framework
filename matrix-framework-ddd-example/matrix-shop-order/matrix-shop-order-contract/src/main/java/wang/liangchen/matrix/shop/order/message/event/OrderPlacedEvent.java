package wang.liangchen.matrix.shop.order.message.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.event.AbstractContractEvent;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 订单已下单事件契约：订单上下文对外发布的"下单已发生"事实（发布语言），
 * 仅携带消费方必要的数据（基本类型值），不依赖领域模型；
 * 由装配器翻译领域事件时复制事件标识值与发生时间。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.EVENT, exchangePattern = MessageExchangePattern.RequestStream)
public final class OrderPlacedEvent extends AbstractContractEvent {

    private final String orderId;
    private final String buyerId;
    private final BigDecimal totalAmount;
    private final String currency;

    @JsonCreator
    public OrderPlacedEvent(@JsonProperty("eventId") String eventId,
                             @JsonProperty("occurredOn") Instant occurredOn,
                             @JsonProperty("orderId") String orderId,
                             @JsonProperty("buyerId") String buyerId,
                             @JsonProperty("totalAmount") BigDecimal totalAmount,
                             @JsonProperty("currency") String currency) {
        super(eventId, occurredOn);
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.totalAmount = totalAmount;
        this.currency = currency;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCurrency() {
        return currency;
    }
}
