package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.northbound.event;

import wang.liangchen.matrix.framework.ddd.northbound.event.IApplicationEvent;

/** 违规：northbound/event包的具体类未继承AbstractApplicationEvent基类 */
public final class BadApplicationEventNotExtendingBase implements IApplicationEvent {
}
