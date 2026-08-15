package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.message;

import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.response.IView;

/** 违规：消息契约提供toXxx()公共工厂方法（契约不得担任工厂） */
@MessageContract(direction = MessageDirection.NORTHBOUND, type = MessageContractType.VIEW, exchangePattern = MessageExchangePattern.RequestResponse)
public final class BadContractFactory implements IView {

    public Object toSomething() {
        return new Object();
    }
}
