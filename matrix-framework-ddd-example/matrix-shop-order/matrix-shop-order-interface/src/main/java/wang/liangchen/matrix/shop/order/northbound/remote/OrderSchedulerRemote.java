package wang.liangchen.matrix.shop.order.northbound.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.liangchen.matrix.framework.ddd.northbound.remote.ISchedulerRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;
import wang.liangchen.matrix.shop.order.message.request.CancelTimeoutOrdersSchedulingRequest;
import wang.liangchen.matrix.shop.order.message.response.CancelTimeoutOrdersResult;
import wang.liangchen.matrix.shop.order.northbound.local.OrderSchedulingApplicationService;

/**
 * 订单调度器远程入口：调度场景的开放主机服务层（定时调度契约），
 * 只操作消息契约，经调度应用服务完成用例编排。
 * <p>
 * 单体与微服务部署均以 Spring 调度按固定周期触发（超时未支付自动取消）；
 * 另提供手动触发端点，供运维补偿与测试验证使用。
 */
@RestController
@RequestMapping("/order-schedulers")
@Remote(RemoteType.Scheduler)
public class OrderSchedulerRemote implements ISchedulerRemote {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSchedulerRemote.class);
    private static final int DEFAULT_TIMEOUT_MINUTES = 30;

    private final OrderSchedulingApplicationService orderSchedulingApplicationService;

    public OrderSchedulerRemote(OrderSchedulingApplicationService orderSchedulingApplicationService) {
        this.orderSchedulingApplicationService = orderSchedulingApplicationService;
    }

    /**
     * 定时触发：超时未支付自动取消（默认阈值30分钟，可经配置调整）。
     */
    @Scheduled(fixedDelayString = "${order.scheduling.cancel-timeout-orders.fixed-delay:30m}")
    public void cancelTimeoutOrdersOnSchedule() {
        try {
            CancelTimeoutOrdersResult result = orderSchedulingApplicationService
                    .cancelTimeoutOrders(new CancelTimeoutOrdersSchedulingRequest(DEFAULT_TIMEOUT_MINUTES));
            LOGGER.info("定时调度取消超时未支付订单：canceledCount={}", result.canceledCount());
        } catch (Exception ex) {
            LOGGER.error("定时调度取消超时未支付订单失败", ex);
        }
    }

    /**
     * 手动触发端点：运维补偿与测试验证使用，携带调度消息契约（超时阈值分钟）。
     */
    @PostMapping("/cancel-timeout-orders")
    public CancelTimeoutOrdersResult cancelTimeoutOrders(@RequestBody(required = false) CancelTimeoutOrdersSchedulingRequest request) {
        return orderSchedulingApplicationService.cancelTimeoutOrders(
                request == null ? new CancelTimeoutOrdersSchedulingRequest(DEFAULT_TIMEOUT_MINUTES) : request);
    }
}
