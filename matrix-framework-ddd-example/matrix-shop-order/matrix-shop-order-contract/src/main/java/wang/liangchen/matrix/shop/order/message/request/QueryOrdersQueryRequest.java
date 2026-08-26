package wang.liangchen.matrix.shop.order.message.request;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.IQueryRequest;

/**
 * 查询买家订单列表请求。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.QUERY_REQUEST, exchangePattern = MessageExchangePattern.RequestResponse)
public record QueryOrdersQueryRequest(String buyerId) implements IQueryRequest {
}
