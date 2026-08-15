package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.message;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.request.ICommandRequest;

/** 违规：实现ICommandRequest但@MessageContract.type为QUERY_REQUEST（类型不匹配） */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.QUERY_REQUEST, exchangePattern = MessageExchangePattern.RequestResponse)
public final class BadCommandWrongType implements ICommandRequest {
}
