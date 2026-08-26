package wang.liangchen.matrix.shop.order.message.response;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.response.IView;

import java.math.BigDecimal;

/**
 * 订单项视图：订单明细中的订单项信息。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.VIEW, exchangePattern = MessageExchangePattern.RequestResponse)
public record OrderItemView(String productId, String productName, BigDecimal price, int quantity, BigDecimal subtotal) implements IView {
}
