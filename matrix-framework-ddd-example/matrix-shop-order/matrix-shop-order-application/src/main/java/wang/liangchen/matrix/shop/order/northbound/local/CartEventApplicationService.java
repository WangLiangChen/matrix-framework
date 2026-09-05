package wang.liangchen.matrix.shop.order.northbound.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IEventApplicationService;
import wang.liangchen.matrix.shop.order.domain.cart.Cart;
import wang.liangchen.matrix.shop.order.domain.order.OrderPlacedEvent;
import wang.liangchen.matrix.shop.order.domain.order.UserId;
import wang.liangchen.matrix.shop.order.southbound.port.CartRepositoryPort;
import wang.liangchen.matrix.shop.order.southbound.port.DomainEventPublisherPort;

/**
 * 购物车事件应用服务：订阅订单上下文内的领域事件，完成跨聚合最终一致协作
 * （一次事务只修改一个聚合实例，订阅方在发布方事务提交后独立处理）。
 * 领域事件在桥接方法中翻译为基本类型值后委托给用例方法
 * （与跨上下文订阅风格一致），降低未来微服务拆分时的改造成本。
 */
@Service
@ApplicationService(ApplicationServiceType.EVENT)
public class CartEventApplicationService implements IEventApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartEventApplicationService.class);

    private final CartRepositoryPort cartRepository;
    private final DomainEventPublisherPort eventPublisher;

    public CartEventApplicationService(CartRepositoryPort cartRepository,
                                       DomainEventPublisherPort eventPublisher) {
        this.cartRepository = cartRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 桥接：订阅"订单已下单"领域事件，翻译为基本类型值后委托给用例方法。
     * 单体部署下由Spring事件总线触发，微服务拆分后由消息契约端点直接调用用例方法。
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPlacedEvent(OrderPlacedEvent event) {
        onOrderPlaced(event.orderId().value(), event.buyerId().value());
    }

    /**
     * 用例：订单已下单后清空该买家的购物车。
     * 订阅方处理失败不影响已提交的下单事务（最终一致性的落点）。
     * 以基本类型值接收事件数据（与跨上下文订阅风格一致），降低未来微服务拆分时的改造成本。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrderPlaced(String orderIdValue, String buyerIdValue) {
        try {
            cartRepository.findByBuyerId(UserId.of(buyerIdValue)).ifPresent(this::clearCart);
            LOGGER.info("订单已下单，购物车已清空：orderId={}, buyerId={}", orderIdValue, buyerIdValue);
        } catch (Exception ex) {
            LOGGER.error("订单已下单后清空购物车失败：orderId={}, buyerId={}", orderIdValue, buyerIdValue, ex);
        }
    }

    private void clearCart(Cart cart) {
        cart.clear();
        cartRepository.save(cart);
        eventPublisher.publish(cart.events());
        cart.clearEvents();
    }
}