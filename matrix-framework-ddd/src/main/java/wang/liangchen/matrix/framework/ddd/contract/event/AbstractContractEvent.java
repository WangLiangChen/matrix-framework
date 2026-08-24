package wang.liangchen.matrix.framework.ddd.contract.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 契约事件基类：跨限界上下文事件契约（发布语言）的基类，具体事件契约使用
 * <p>
 * 消息契约不依赖领域模型（消息契约可能单独提供给下游，不得携带领域模型依赖）：
 * 领域事件向外发布前由装配器翻译为继承本类的事件契约（复制事件标识值与发生时间）。
 * <p>
 * 值相等：按事件标识与类型比较，支撑事件幂等消费（以eventId为准）。
 * <p>
 * 构造说明：默认构造使用系统时钟与随机eventId（正常业务路径）；
 * 注入构造(eventId, occurredOn)用于测试与历史事件回放，保证事件的确定性与可复现。
 *
 * @author Liangchen.Wang
 */
public abstract class AbstractContractEvent implements IContractEvent {

    private final String eventId;
    private final Instant occurredOn;

    protected AbstractContractEvent() {
        this(UUID.randomUUID().toString(), Instant.now());
    }

    protected AbstractContractEvent(String eventId, Instant occurredOn) {
        this.eventId = Objects.requireNonNull(eventId, "eventId must not be null");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractContractEvent that)) {
            return false;
        }
        return getClass().equals(that.getClass()) && eventId.equals(that.eventId);
    }

    @Override
    public int hashCode() {
        return 31 * getClass().hashCode() + eventId.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(eventId=" + eventId + ", occurredOn=" + occurredOn + ")";
    }
}
