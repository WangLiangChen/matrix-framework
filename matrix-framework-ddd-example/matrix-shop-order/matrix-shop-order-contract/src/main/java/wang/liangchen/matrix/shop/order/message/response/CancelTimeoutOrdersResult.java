package wang.liangchen.matrix.shop.order.message.response;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.response.IResult;

/**
 * 取消超时未支付订单调度响应：返回本次调度实际取消的订单数。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.RESULT, exchangePattern = MessageExchangePattern.RequestResponse)
public record CancelTimeoutOrdersResult(int canceledCount) implements IResult {
}
