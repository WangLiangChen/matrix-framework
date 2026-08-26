package wang.liangchen.matrix.shop.product.message.request;
import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.IQueryRequest;


/**
 * 查询类目树请求。
 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.QUERY_REQUEST, exchangePattern = MessageExchangePattern.RequestResponse)
public record CategoryQueryRequest() implements IQueryRequest {
}
