package wang.liangchen.matrix.framework.ddd.domain.event;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.identity.AbstractIdentity;
import wang.liangchen.matrix.framework.ddd.domain.identity.IStringIdentity;
import wang.liangchen.matrix.framework.ddd.domain.identity.UUIDIdentity;

import java.util.Objects;

/**
 * 领域事件的唯一标识：通用类型（无业务含义）的身份标识。
 * 组合UUIDIdentity复用其UUID格式校验、of/next静态工厂与值相等语义；事件幂等消费可依赖其值比较。
 *
 * @author Liangchen.Wang
 */
@DomainModel(DomainMetaModel.Identity)
public final class EventId extends AbstractIdentity implements IStringIdentity {

    private final UUIDIdentity value;

    private EventId(UUIDIdentity value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public static EventId of(String value) {
        return new EventId(UUIDIdentity.of(value));
    }

    public static EventId next() {
        return new EventId(UUIDIdentity.next());
    }

    @Override
    public String value() {
        return value.value();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
