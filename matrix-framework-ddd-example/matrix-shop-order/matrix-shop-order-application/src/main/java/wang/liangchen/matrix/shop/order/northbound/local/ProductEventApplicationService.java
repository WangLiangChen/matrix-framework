package wang.liangchen.matrix.shop.order.northbound.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IEventApplicationService;
import wang.liangchen.matrix.shop.order.domain.cart.Cart;
import wang.liangchen.matrix.shop.order.domain.port.CartRepositoryPort;
import wang.liangchen.matrix.shop.order.domain.port.DomainEventPublisherPort;
import wang.liangchen.matrix.shop.order.domain.shared.ProductId;

/**
 * 商品事件应用服务：订阅商品上下文对外发布的契约事件（发布语言），
 * 以基本类型值接收（防腐层不引入上游契约类型），完成跨上下文最终一致协作。
 */
@Service
@ApplicationService(ApplicationServiceType.EVENT)
public class ProductEventApplicationService implements IEventApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventApplicationService.class);

    private final CartRepositoryPort cartRepository;
    private final DomainEventPublisherPort eventPublisher;

    public ProductEventApplicationService(CartRepositoryPort cartRepository,
                                          DomainEventPublisherPort eventPublisher) {
        this.cartRepository = cartRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 订阅"商品已下架"：从所有包含该商品的购物车中移除该商品。
     * 进程内订阅路径在发布方事务提交后（AFTER_COMMIT）触发，须以独立事务提交订阅方的变更。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProductDelisted(String productIdValue) {
        UseCases.execute("商品下架移除购物车商品", () -> {
            ProductId productId = ProductId.of(productIdValue);
            cartRepository.cartsContaining(productId).forEach(cart -> removeCartItem(cart, productId));
            LOGGER.info("商品已下架，包含该商品的购物车已处理：productId={}", productIdValue);
        });
    }

    private void removeCartItem(Cart cart, ProductId productId) {
        cart.removeItem(productId);
        cartRepository.save(cart);
        eventPublisher.publish(cart.events());
        cart.clearEvents();
    }
}
