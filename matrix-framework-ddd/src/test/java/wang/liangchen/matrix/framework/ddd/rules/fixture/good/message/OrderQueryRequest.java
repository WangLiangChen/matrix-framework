package wang.liangchen.matrix.framework.ddd.rules.fixture.good.message;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.IQueryRequest;

/** 合规查询请求契约 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.QUERY_REQUEST, exchangePattern = MessageExchangePattern.RequestResponse)
public final class OrderQueryRequest implements IQueryRequest {
}
