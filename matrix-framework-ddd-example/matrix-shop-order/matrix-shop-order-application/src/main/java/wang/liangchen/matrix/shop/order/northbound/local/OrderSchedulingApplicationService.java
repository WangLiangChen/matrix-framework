package wang.liangchen.matrix.shop.order.northbound.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.ISchedulingApplicationService;
import wang.liangchen.matrix.shop.order.domain.order.Order;
import wang.liangchen.matrix.shop.order.southbound.port.OrderRepositoryPort;
import wang.liangchen.matrix.shop.order.message.request.CancelOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.request.CancelTimeoutOrdersSchedulingRequest;
import wang.liangchen.matrix.shop.order.message.response.CancelTimeoutOrdersResult;
import wang.liangchen.matrix.shop.order.northbound.exception.ApplicationException;
import wang.liangchen.matrix.shop.order.service.OrderCommandService;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * 订单调度应用服务：编排定时用例（超时未支付自动取消订单）。
 * 调度本身不是一个跨聚合的大事务：经查询读侧找出超时未支付订单后，
 * 逐个复用"取消订单"命令用例（各自独立事务，一次事务只修改一个聚合实例），
 * 单个订单取消失败不影响其余订单（失败仅记录日志）。
 */
@Service
@ApplicationService(ApplicationServiceType.SCHEDULING)
public class OrderSchedulingApplicationService implements ISchedulingApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSchedulingApplicationService.class);

    private final OrderRepositoryPort orderRepository;
    private final OrderCommandService orderCommandService;

    public OrderSchedulingApplicationService(OrderRepositoryPort orderRepository,
                                              OrderCommandService orderCommandService) {
        this.orderRepository = orderRepository;
        this.orderCommandService = orderCommandService;
    }

    /**
     * 用例：取消超时未支付订单——取消下单超过阈值分钟仍待支付的订单。
     */
    public CancelTimeoutOrdersResult cancelTimeoutOrders(CancelTimeoutOrdersSchedulingRequest request) {
        Instant deadline = Instant.now().minus(Duration.ofMinutes(request.timeoutMinutes()));
        List<Order> unpaidOrders = orderRepository.findUnpaidCreatedBefore(deadline);
        int canceledCount = 0;
        for (Order order : unpaidOrders) {
            try {
                orderCommandService.cancelOrder(new CancelOrderCommandRequest(order.id().value()));
                canceledCount++;
            } catch (ApplicationException ex) {
                LOGGER.warn("超时未支付订单自动取消失败：orderId={}", order.id().value(), ex);
            }
        }
        LOGGER.info("超时未支付订单调度完成：timeoutMinutes={}, matched={}, canceled={}",
                request.timeoutMinutes(), unpaidOrders.size(), canceledCount);
        return new CancelTimeoutOrdersResult(canceledCount);
    }
}
