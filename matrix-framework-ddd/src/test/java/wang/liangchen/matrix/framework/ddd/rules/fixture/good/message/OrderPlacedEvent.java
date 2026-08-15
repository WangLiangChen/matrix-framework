package wang.liangchen.matrix.framework.ddd.rules.fixture.good.message;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.event.AbstractContractEvent;

/** 合规事件契约：继承AbstractEventContract，业务名词+过去式动词命名 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.EVENT, exchangePattern = MessageExchangePattern.RequestStream)
public final class OrderPlacedEvent extends AbstractContractEvent {

    private final String orderId;

    public OrderPlacedEvent(String orderId) {
        super();
        this.orderId = orderId;
    }
}
