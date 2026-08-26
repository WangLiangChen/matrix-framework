package wang.liangchen.matrix.shop.order.message.response;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.response.IView;
import wang.liangchen.matrix.shop.order.message.request.PlaceOrderCommandRequest.Receiver;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单明细视图：订单详情查询视图。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.VIEW, exchangePattern = MessageExchangePattern.RequestResponse)
public record OrderDetailView(String id, String buyerId, Receiver receiver,
                              List<OrderItemView> items, String status, BigDecimal totalAmount) implements IView {
}
