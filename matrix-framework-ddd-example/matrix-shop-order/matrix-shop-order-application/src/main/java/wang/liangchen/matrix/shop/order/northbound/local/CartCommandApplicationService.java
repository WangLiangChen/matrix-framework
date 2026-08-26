package wang.liangchen.matrix.shop.order.northbound.local;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.ICommandApplicationService;
import wang.liangchen.matrix.shop.order.domain.cart.Cart;
import wang.liangchen.matrix.shop.order.domain.cart.CartFactory;
import wang.liangchen.matrix.shop.order.domain.cart.CartId;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.domain.order.ProductId;
import wang.liangchen.matrix.shop.order.domain.order.UserId;
import wang.liangchen.matrix.shop.order.domain.port.CartRepositoryPort;
import wang.liangchen.matrix.shop.order.domain.port.DomainEventPublisherPort;
import wang.liangchen.matrix.shop.order.domain.port.ProductClientPort;
import wang.liangchen.matrix.shop.order.domain.readmodel.ProductSummary;
import wang.liangchen.matrix.shop.order.message.request.AddCartItemCommandRequest;
import wang.liangchen.matrix.shop.order.message.request.ChangeCartItemQuantityCommandRequest;
import wang.liangchen.matrix.shop.order.message.request.ClearCartCommandRequest;
import wang.liangchen.matrix.shop.order.message.request.RemoveCartItemCommandRequest;
import wang.liangchen.matrix.shop.order.message.response.AddCartItemResult;
import wang.liangchen.matrix.shop.order.message.response.ChangeCartItemQuantityResult;
import wang.liangchen.matrix.shop.order.message.response.ClearCartResult;
import wang.liangchen.matrix.shop.order.message.response.RemoveCartItemResult;
import wang.liangchen.matrix.shop.order.northbound.exception.ApplicationException;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 购物车命令应用服务：编排购物车聚合实现命令用例，
 * 加购时通过商品客户端端口获取商品名称与最低售价快照。
 */
@Service
@ApplicationService(ApplicationServiceType.COMMAND)
public class CartCommandApplicationService implements ICommandApplicationService {

    private final CartRepositoryPort cartRepository;
    private final ProductClientPort productClient;
    private final DomainEventPublisherPort eventPublisher;
    private final CartFactory cartFactory = new CartFactory();

    public CartCommandApplicationService(CartRepositoryPort cartRepository,
                                         ProductClientPort productClient,
                                         DomainEventPublisherPort eventPublisher) {
        this.cartRepository = cartRepository;
        this.productClient = productClient;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 用例：加入购物车，购物车不存在时创建空购物车。
     */
    @Transactional
    public AddCartItemResult addCartItem(AddCartItemCommandRequest request) {
        return useCase("加入购物车", () -> {
            CartId cartId = CartId.of(request.cartId());
            Cart cart = cartRepository.findById(cartId)
                    .orElseGet(() -> cartFactory.create(cartId, UserId.of(request.buyerId())));
            ProductSummary summary = productClient.obtainProduct(ProductId.of(request.productId()));
            cart.addItem(summary.id(), summary.productName(), summary.minPrice(), request.quantity());
            cartRepository.save(cart);
            eventPublisher.publish(cart.events());
            cart.clearEvents();
            return new AddCartItemResult(cart.id().value(), request.productId(), request.quantity());
        });
    }

    /**
     * 用例：变更购物车商品数量。
     */
    @Transactional
    public ChangeCartItemQuantityResult changeCartItemQuantity(ChangeCartItemQuantityCommandRequest request) {
        return useCase("变更购物车商品数量", () -> {
            Cart cart = mutate(CartId.of(request.cartId()),
                    c -> c.changeItemQuantity(ProductId.of(request.productId()), request.quantity()));
            return new ChangeCartItemQuantityResult(cart.id().value(), request.productId(), request.quantity());
        });
    }

    /**
     * 用例：移除购物车商品。
     */
    @Transactional
    public RemoveCartItemResult removeCartItem(RemoveCartItemCommandRequest request) {
        return useCase("移除购物车商品", () -> {
            Cart cart = mutate(CartId.of(request.cartId()),
                    c -> c.removeItem(ProductId.of(request.productId())));
            return new RemoveCartItemResult(cart.id().value(), request.productId());
        });
    }

    /**
     * 用例：清空购物车。
     */
    @Transactional
    public ClearCartResult clearCart(ClearCartCommandRequest request) {
        return useCase("清空购物车", () -> {
            Cart cart = mutate(CartId.of(request.cartId()), Cart::clear);
            return new ClearCartResult(cart.id().value());
        });
    }

    private Cart mutate(CartId cartId, Consumer<Cart> mutation) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new DomainException("购物车不存在：" + cartId.value()));
        mutation.accept(cart);
        cartRepository.save(cart);
        eventPublisher.publish(cart.events());
        cart.clearEvents();
        return cart;
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
