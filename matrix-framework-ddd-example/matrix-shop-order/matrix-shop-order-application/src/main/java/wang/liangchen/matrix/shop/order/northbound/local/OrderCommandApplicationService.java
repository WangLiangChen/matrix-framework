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
import wang.liangchen.matrix.shop.order.domain.readmodel.ProductSummary;
import wang.liangchen.matrix.shop.order.message.request.*;
import wang.liangchen.matrix.shop.order.message.response.*;
import wang.liangchen.matrix.shop.order.northbound.exception.ApplicationException;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 订单命令应用服务：编排订单聚合实现命令用例，一个用例对应一个事务，
 * 一次事务只修改一个订单聚合实例，下单时通过商品客户端端口获取商品快照。
 */
@Service
@ApplicationService(ApplicationServiceType.COMMAND)
public class OrderCommandApplicationService implements ICommandApplicationService {

    private final OrderRepositoryPort orderRepository;
    private final ProductClientPort productClient;
    private final DomainEventPublisherPort eventPublisher;
    private final OrderFactory orderFactory = new OrderFactory();

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
        return useCase("下单", () -> {
            List<OrderItemTemplate> templates = request.items().stream()
                    .map(item -> itemTemplate(item.productId(), item.quantity()))
                    .toList();
            Address receiver = new Address(request.receiver().receiver(), request.receiver().phone(),
                    request.receiver().province(), request.receiver().city(), request.receiver().detail());
            Order order = orderFactory.create(UserId.of(request.buyerId()), receiver, templates);
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
        return useCase("支付订单", () -> {
            Order order = mutate(OrderId.of(request.orderId()), Order::pay);
            return new PayOrderResult(order.id().value(), order.status().name());
        });
    }

    /**
     * 用例：发货。
     */
    @Transactional
    public ShipOrderResult shipOrder(ShipOrderCommandRequest request) {
        return useCase("发货", () -> {
            Order order = mutate(OrderId.of(request.orderId()), Order::ship);
            return new ShipOrderResult(order.id().value(), order.status().name());
        });
    }

    /**
     * 用例：完成订单。
     */
    @Transactional
    public CompleteOrderResult completeOrder(CompleteOrderCommandRequest request) {
        return useCase("完成订单", () -> {
            Order order = mutate(OrderId.of(request.orderId()), Order::complete);
            return new CompleteOrderResult(order.id().value(), order.status().name());
        });
    }

    /**
     * 用例：取消订单。
     */
    @Transactional
    public CancelOrderResult cancelOrder(CancelOrderCommandRequest request) {
        return useCase("取消订单", () -> {
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
        return new OrderItemTemplate(summary.id(), summary.productName(), summary.minPrice(), quantity);
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
