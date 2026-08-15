package wang.liangchen.matrix.framework.ddd.rules.fixture.good.message;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.response.IView;

/** 合规查询视图契约 */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.VIEW, exchangePattern = MessageExchangePattern.RequestResponse)
public final class OrderView implements IView {

    private final String orderId;
    private final String customer;

    public OrderView(String orderId, String customer) {
        this.orderId = orderId;
        this.customer = customer;
    }
}
