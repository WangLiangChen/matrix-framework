package wang.liangchen.matrix.framework.ddd.rules.fixture.good.northbound.event;

import wang.liangchen.matrix.framework.ddd.northbound.event.AbstractApplicationEvent;

/** 合规应用事件：继承AbstractApplicationEvent，仅限进程内应用层协作与通知 */
public final class OrderPlacedApplicationEvent extends AbstractApplicationEvent {

    private final String orderId;

    public OrderPlacedApplicationEvent(String orderId) {
        super();
        this.orderId = orderId;
    }
}
