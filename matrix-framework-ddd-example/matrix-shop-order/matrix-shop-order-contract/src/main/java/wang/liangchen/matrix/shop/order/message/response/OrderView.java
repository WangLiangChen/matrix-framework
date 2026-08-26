package wang.liangchen.matrix.shop.order.message.response;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.response.IView;

import java.math.BigDecimal;

/**
 * 订单视图：订单列表查询视图。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.VIEW, exchangePattern = MessageExchangePattern.RequestResponse)
public record OrderView(String id, String buyerId, String status, BigDecimal totalAmount) implements IView {
}
