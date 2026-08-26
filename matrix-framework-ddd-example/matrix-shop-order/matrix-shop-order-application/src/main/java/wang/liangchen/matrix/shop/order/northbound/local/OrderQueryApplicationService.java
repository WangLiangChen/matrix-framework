package wang.liangchen.matrix.shop.order.northbound.local;

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.domain.order.Address;
import wang.liangchen.matrix.shop.order.domain.order.OrderId;
import wang.liangchen.matrix.shop.order.domain.order.UserId;
import wang.liangchen.matrix.shop.order.domain.port.OrderQueryPort;
import wang.liangchen.matrix.shop.order.domain.readmodel.OrderDetail;
import wang.liangchen.matrix.shop.order.domain.readmodel.OrderItemSummary;
import wang.liangchen.matrix.shop.order.domain.readmodel.OrderSummary;
import wang.liangchen.matrix.shop.order.message.request.OrderQueryRequest;
import wang.liangchen.matrix.shop.order.message.request.PlaceOrderCommandRequest.Receiver;
import wang.liangchen.matrix.shop.order.message.request.QueryOrdersQueryRequest;
import wang.liangchen.matrix.shop.order.message.response.OrderDetailView;
import wang.liangchen.matrix.shop.order.message.response.OrderItemView;
import wang.liangchen.matrix.shop.order.message.response.OrderView;
import wang.liangchen.matrix.shop.order.northbound.exception.ApplicationException;

import java.util.List;
import java.util.function.Supplier;

/**
 * 订单查询应用服务：CQRS查询侧，只读访问订单读模型，
 * 将读模型装配为查询视图。
 */
@Service
@ApplicationService(ApplicationServiceType.QUERY)
public class OrderQueryApplicationService implements IQueryApplicationService {

    private final OrderQueryPort orderQuery;

    public OrderQueryApplicationService(OrderQueryPort orderQuery) {
        this.orderQuery = orderQuery;
    }

    /**
     * 用例：查询订单明细。
     */
    public OrderDetailView queryOrder(OrderQueryRequest request) {
        return useCase("查询订单明细", () -> {
            OrderDetail detail = orderQuery.queryById(OrderId.of(request.orderId()))
                    .orElseThrow(() -> new DomainException("订单不存在：" + request.orderId()));
            return new OrderDetailView(detail.id().value(), detail.buyerId().value(), receiver(detail.receiver()),
                    detail.items().stream().map(this::orderItemView).toList(),
                    detail.status().name(), detail.totalAmount().amount());
        });
    }

    /**
     * 用例：查询买家订单列表。
     */
    public List<OrderView> queryOrders(QueryOrdersQueryRequest request) {
        return useCase("查询订单列表", () -> orderQuery.queryByBuyerId(UserId.of(request.buyerId())).stream()
                .map(this::orderView)
                .toList());
    }

    private OrderView orderView(OrderSummary summary) {
        return new OrderView(summary.id().value(), summary.buyerId().value(),
                summary.status().name(), summary.totalAmount().amount());
    }

    private OrderItemView orderItemView(OrderItemSummary item) {
        return new OrderItemView(item.productId().value(), item.productName(),
                item.unitPrice().amount(), item.quantity(), item.unitPrice().multiply(item.quantity()).amount());
    }

    private Receiver receiver(Address address) {
        return new Receiver(address.receiver(), address.phone(), address.province(), address.city(), address.detail());
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
