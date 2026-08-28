package wang.liangchen.matrix.shop.order.northbound.local;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.ICommandApplicationService;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.domain.order.*;
import wang.liangchen.matrix.shop.order.domain.port.DomainEventPublisherPort;
import wang.liangchen.matrix.shop.order.domain.port.OrderRepositoryPort;
import wang.liangchen.matrix.shop.order.domain.port.ProductClientPort;
import wang.liangchen.matrix.shop.order.domain.shared.ProductId;
import wang.liangchen.matrix.shop.order.domain.shared.ProductSummary;
import wang.liangchen.matrix.shop.order.message.request.*;
import wang.liangchen.matrix.shop.order.message.response.*;
import wang.liangchen.matrix.shop.order.northbound.assembler.OrderAssembler;
import wang.liangchen.matrix.shop.order.northbound.exception.ApplicationException;
import wang.liangchen.matrix.shop.order.service.OrderCommandService;

import java.util.List;
import java.util.function.Consumer;

/**
 * 订单命令应用服务：编排订单聚合实现命令用例，一个用例对应一个事务，
 * 一次事务只修改一个订单聚合实例，下单时通过商品客户端端口获取商品快照，
 * 经订单装配器完成消息契约与领域值对象之间的装配。
 * 同时是 contract 中应用服务接口 OrderCommandService 的本地实现（单体形态），
 * 微服务形态由 client 模块的 OrderFeignClientAdapter 远程实现。
 */
@Service
@ApplicationService(ApplicationServiceType.COMMAND)
public class OrderCommandApplicationService implements ICommandApplicationService, OrderCommandService {

    private final OrderRepositoryPort orderRepository;
    private final ProductClientPort productClient;
    private final DomainEventPublisherPort eventPublisher;
    private final OrderFactory orderFactory = new OrderFactory();
    private final OrderAssembler orderAssembler = new OrderAssembler();

    public OrderCommandApplicationService(OrderRepositoryPort orderRepository,
                                          ProductClientPort productClient,
                                          DomainEventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 用例：下单。
     */
    @Transactional
    public PlaceOrderResult placeOrder(PlaceOrderCommandRequest request) {
        return UseCases.execute("下单", () -> {
            List<OrderItemTemplate> templates = request.items().stream()
                    .map(item -> itemTemplate(item.productId(), item.quantity()))
                    .toList();
            Address receiver = orderAssembler.toAddress(request.receiver());
            Order order = orderFactory.create(UserId.of(request.buyerId()), receiver, templates,
                    LoyaltyLevel.of(request.loyaltyLevel()));
            orderRepository.save(order);
            eventPublisher.publish(order.events());
            order.clearEvents();
            return new PlaceOrderResult(order.id().value(), order.totalAmount().amount());
        });
    }

    /**
     * 用例：支付订单。
     */
    @Transactional
    public PayOrderResult payOrder(PayOrderCommandRequest request) {
        return UseCases.execute("支付订单", () -> {
            Order order = mutate(OrderId.of(request.orderId()), Order::pay);
            return new PayOrderResult(order.id().value(), order.status().name());
        });
    }

    /**
     * 用例：发货。
     */
    @Transactional
    public ShipOrderResult shipOrder(ShipOrderCommandRequest request) {
        return UseCases.execute("发货", () -> {
            Order order = mutate(OrderId.of(request.orderId()), Order::ship);
            return new ShipOrderResult(order.id().value(), order.status().name());
        });
    }

    /**
     * 用例：完成订单。
     */
    @Transactional
    public CompleteOrderResult completeOrder(CompleteOrderCommandRequest request) {
        return UseCases.execute("完成订单", () -> {
            Order order = mutate(OrderId.of(request.orderId()), Order::complete);
            return new CompleteOrderResult(order.id().value(), order.status().name());
        });
    }

    /**
     * 用例：取消订单。
     */
    @Transactional
    public CancelOrderResult cancelOrder(CancelOrderCommandRequest request) {
        return UseCases.execute("取消订单", () -> {
            Order order = mutate(OrderId.of(request.orderId()), Order::cancel);
            return new CancelOrderResult(order.id().value(), order.status().name());
        });
    }

    private Order mutate(OrderId orderId, Consumer<Order> mutation) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException("订单不存在：" + orderId.value()));
        mutation.accept(order);
        orderRepository.save(order);
        eventPublisher.publish(order.events());
        order.clearEvents();
        return order;
    }

    private OrderItemTemplate itemTemplate(String productIdValue, int quantity) {
        ProductSummary summary = productClient.obtainProduct(ProductId.of(productIdValue));
        return orderAssembler.toOrderItemTemplate(summary, quantity);
    }
}
