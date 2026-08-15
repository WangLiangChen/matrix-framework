package wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.entity.IEntity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;

/** 合规聚合内部实体：包内可见，仅经聚合根访问；相等性按身份标识定义 */
@DomainModel(DomainMetaModel.Entity)
class OrderItem implements IEntity<OrderItemId> {

    @Identity
    private final OrderItemId orderItemId;

    OrderItem(OrderItemId orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OrderItem that)) {
            return false;
        }
        return orderItemId.equals(that.orderItemId);
    }

    @Override
    public int hashCode() {
        return orderItemId.hashCode();
    }
}
