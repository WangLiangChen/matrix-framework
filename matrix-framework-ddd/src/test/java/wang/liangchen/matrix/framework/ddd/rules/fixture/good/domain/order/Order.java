package wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AbstractAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;

/** 合规聚合根：唯一入口，业务行为收集领域事件；相等性按身份标识定义；
 * 聚合包内存在领域工厂，构造方法包内可见，聚合根只能经工厂创建 */
@DomainModel(DomainMetaModel.AggregateRoot)
public final class Order extends AbstractAggregateRoot<OrderId> implements IAggregateRoot<OrderId> {

    @Identity
    private final OrderId orderId;

    private String customer;

    Order(OrderId orderId, String customer) {
        this.orderId = orderId;
        this.customer = customer;
    }

    public void place() {
        // 重要状态变化：发布领域事件（过去时命名）
        raise(new OrderPlaced(orderId.value()));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Order that)) {
            return false;
        }
        return orderId.equals(that.orderId);
    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }
}
