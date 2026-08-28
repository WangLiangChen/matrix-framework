package wang.liangchen.matrix.shop.order.message.request;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ISchedulingRequest;

/**
 * 取消超时未支付订单调度消息：定时任务与手动触发共用的调度契约，
 * 携带超时阈值（分钟），通知订单上下文取消超过该时长仍未支付的订单。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.SCHEDULING, exchangePattern = MessageExchangePattern.RequestResponse)
public record CancelTimeoutOrdersSchedulingRequest(int timeoutMinutes) implements ISchedulingRequest {

    public CancelTimeoutOrdersSchedulingRequest {
        if (timeoutMinutes < 0) {
            throw new IllegalArgumentException("超时分钟数不能为负数");
        }
    }
}
