package wang.liangchen.matrix.framework.ddd.rules.fixture.good.domain.order;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.factory.IDomainFactory;

/** 合规领域工厂：create创建新聚合，reconstitute从持久化数据重建聚合 */
@DomainModel(DomainMetaModel.DomainFactory)
public final class OrderFactory implements IDomainFactory {

    public Order create(OrderId orderId, String customer) {
        return new Order(orderId, customer);
    }

    public Order reconstitute(OrderId orderId, String customer) {
        return new Order(orderId, customer);
    }
}
