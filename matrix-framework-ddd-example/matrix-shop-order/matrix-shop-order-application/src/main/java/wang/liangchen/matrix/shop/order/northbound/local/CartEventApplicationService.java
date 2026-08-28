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
import wang.liangchen.matrix.shop.order.domain.order.OrderPlaced;
import wang.liangchen.matrix.shop.order.domain.port.CartRepositoryPort;
import wang.liangchen.matrix.shop.order.domain.port.DomainEventPublisherPort;

/**
 * 购物车事件应用服务：订阅订单上下文内的领域事件，完成跨聚合最终一致协作
 * （一次事务只修改一个聚合实例，订阅方在发布方事务提交后独立处理）。
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
     * 订阅"订单已下单"：下单事务提交后清空该买家的购物车。
     * 订阅方处理失败不影响已提交的下单事务（最终一致性的落点）。
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrderPlaced(OrderPlaced event) {
        try {
            cartRepository.findByBuyerId(event.buyerId()).ifPresent(this::clearCart);
            LOGGER.info("订单已下单，购物车已清空：orderId={}, buyerId={}",
                    event.orderId().value(), event.buyerId().value());
        } catch (Exception ex) {
            LOGGER.error("订单已下单后清空购物车失败：orderId={}, buyerId={}",
                    event.orderId().value(), event.buyerId().value(), ex);
        }
    }

    private void clearCart(Cart cart) {
        cart.clear();
        cartRepository.save(cart);
        eventPublisher.publish(cart.events());
        cart.clearEvents();
    }
}
