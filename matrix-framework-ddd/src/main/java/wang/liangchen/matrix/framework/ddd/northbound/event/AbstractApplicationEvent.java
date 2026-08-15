package wang.liangchen.matrix.framework.ddd.northbound.event;

import wang.liangchen.matrix.framework.ddd.contract.event.AbstractContractEvent;

import java.time.Instant;

/**
 * 应用事件基类：由应用服务发布的事件，面向应用层的协作与通知，区别于表达业务事实的领域事件。
 * 继承AbstractContractEvent复用事件唯一标识(eventId)与事件发生时间(occurredOn)及其值相等语义。
 * <p>
 * 应用事件仅限进程内（应用层）协作与通知，不跨限界上下文发布，不建议携带领域对象引用；
 * 跨限界上下文的事件契约（发布语言）不继承本类，直接继承contract.event.AbstractContractEvent；
 * 领域事件向外发布前由装配器翻译为事件契约（复制事件标识值与发生时间）。
 *
 * @author Liangchen.Wang
 */
public abstract class AbstractApplicationEvent extends AbstractContractEvent implements IApplicationEvent {

    protected AbstractApplicationEvent() {
        super();
    }

    protected AbstractApplicationEvent(String eventId, Instant occurredOn) {
        super(eventId, occurredOn);
    }
}
