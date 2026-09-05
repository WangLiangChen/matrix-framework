package wang.liangchen.matrix.shop.order.northbound.local;

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.domain.order.Order;
import wang.liangchen.matrix.shop.order.domain.order.OrderId;
import wang.liangchen.matrix.shop.order.domain.order.UserId;
import wang.liangchen.matrix.shop.order.southbound.port.OrderRepositoryPort;
import wang.liangchen.matrix.shop.order.message.request.OrderQueryRequest;
import wang.liangchen.matrix.shop.order.message.request.QueryOrdersQueryRequest;
import wang.liangchen.matrix.shop.order.message.response.OrderDetailView;
import wang.liangchen.matrix.shop.order.message.response.OrderView;
import wang.liangchen.matrix.shop.order.northbound.assembler.OrderAssembler;
import wang.liangchen.matrix.shop.order.service.OrderQueryService;

import java.util.List;

/**
 * 订单查询应用服务：CQRS查询侧，经订单仓储端口只读获取订单聚合，
 * 经订单装配器将聚合装配为查询视图。
 * 同时是 contract 中应用服务接口 OrderQueryService 的本地实现（单体形态），
 * 微服务形态由 client 模块的 OrderFeignClientAdapter 远程实现。
 */
@Service
@ApplicationService(ApplicationServiceType.QUERY)
public class OrderQueryApplicationService implements IQueryApplicationService, OrderQueryService {

    private final OrderRepositoryPort orderRepository;
    private final OrderAssembler orderAssembler = new OrderAssembler();

    public OrderQueryApplicationService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * 用例：查询订单明细。
     */
    public OrderDetailView queryOrder(OrderQueryRequest request) {
        return UseCases.execute("查询订单明细", () -> {
            Order order = orderRepository.findById(OrderId.of(request.orderId()))
                    .orElseThrow(() -> new DomainException("订单不存在：" + request.orderId()));
            return orderAssembler.toOrderDetailView(order);
        });
    }

    /**
     * 用例：查询买家订单列表。
     */
    public List<OrderView> queryOrders(QueryOrdersQueryRequest request) {
        return UseCases.execute("查询订单列表", () -> orderRepository.findByBuyerId(UserId.of(request.buyerId())).stream()
                .map(orderAssembler::toOrderView)
                .toList());
    }
}
