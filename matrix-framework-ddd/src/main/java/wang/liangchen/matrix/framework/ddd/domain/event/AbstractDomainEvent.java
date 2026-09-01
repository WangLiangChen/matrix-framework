package wang.liangchen.matrix.framework.ddd.domain.event;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 领域事件基类：事件是已经发生的业务事实，不可变，不携带发布者引用，不继承java.util.EventObject。
 * 只提供事件唯一标识(eventId)与事件发生时间(occurredOn)两项基础属性，业务数据由具体事件以消费方必要数据为限自行携带；
 * 源聚合标识由具体事件类以业务命名字段携带（如orderId），不进基类；
 * eventType/version/traceId等技术与契约关注点分别由序列化器、消息契约、消息信封承担。
 * <p>
 * 领域对象不可序列化直传：事件跨边界传输由消息总线以发布语言（JSON/Protobuf等）序列化，故不实现Serializable。
 * <p>
 * 构造说明：默认构造使用系统时钟与随机eventId（正常业务路径）；
 * 注入构造(eventId, occurredOn)用于测试、reconstitute与历史事件回放，保证事件的确定性与可复现。
 *
 * @author Liangchen.Wang
 */
@DomainModel(DomainMetaModel.DomainEvent)
public abstract class AbstractDomainEvent implements IDomainEvent {

    private final String eventId;
    private final Instant occurredOn;

    protected AbstractDomainEvent() {
        this(UUID.randomUUID().toString(), Instant.now());
    }

    protected AbstractDomainEvent(String eventId, Instant occurredOn) {
        this.eventId = Objects.requireNonNull(eventId, "eventId must not be null");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    /**
     * 值相等：按事件标识与类型比较，支撑事件幂等消费（以eventId为准）。
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractDomainEvent that)) {
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
