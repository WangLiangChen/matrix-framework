package wang.liangchen.matrix.framework.ddd.domain.aggregate;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.entity.AbstractEntity;
import wang.liangchen.matrix.framework.ddd.domain.event.IDomainEvent;
import wang.liangchen.matrix.framework.ddd.domain.identity.IIdentity;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 聚合根基类：聚合根是实体的特例，是聚合的唯一入口，负责维护聚合内部不变式(必须始终保持为真的业务规则或约束条件)。
 * 提供领域事件收集：聚合在业务行为中通过raise收集事件，
 * 应用服务在事务提交后通过events()/clearEvents()提取并统一发布，避免事件与业务变更不一致。
 *
 * @author Liangchen.Wang
 */
@DomainModel(DomainMetaModel.AggregateRoot)
public abstract class AbstractAggregateRoot<ID extends IIdentity> extends AbstractEntity<ID> implements IAggregateRoot<ID> {

    private final Queue<IDomainEvent> domainEvents = new ConcurrentLinkedQueue<>();

    /**
     * 收集领域事件：由聚合的业务方法在重要状态变化时调用。
     */
    protected void raise(IDomainEvent domainEvent) {
        Objects.requireNonNull(domainEvent, "domainEvent must not be null");
        domainEvents.add(domainEvent);
    }

    /**
     * 提取已收集但未发布的领域事件（只读快照）。
     */
    public List<IDomainEvent> events() {
        return List.copyOf(domainEvents);
    }

    /**
     * 清空已收集的领域事件：由应用服务在统一发布后调用。
     */
    public void clearEvents() {
        domainEvents.clear();
    }
}
