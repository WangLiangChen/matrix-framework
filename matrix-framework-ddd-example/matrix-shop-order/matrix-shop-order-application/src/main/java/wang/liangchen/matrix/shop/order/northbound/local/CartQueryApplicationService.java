package wang.liangchen.matrix.shop.order.northbound.local;

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;
import wang.liangchen.matrix.shop.order.domain.cart.Cart;
import wang.liangchen.matrix.shop.order.domain.cart.CartId;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.southbound.port.CartRepositoryPort;
import wang.liangchen.matrix.shop.order.message.request.CartQueryRequest;
import wang.liangchen.matrix.shop.order.message.response.CartView;
import wang.liangchen.matrix.shop.order.northbound.assembler.CartAssembler;

/**
 * 购物车查询应用服务：CQRS查询侧，经购物车仓储端口只读获取购物车聚合并装配为视图。
 */
@Service
@ApplicationService(ApplicationServiceType.QUERY)
public class CartQueryApplicationService implements IQueryApplicationService {

    private final CartRepositoryPort cartRepository;
    private final CartAssembler cartAssembler = new CartAssembler();

    public CartQueryApplicationService(CartRepositoryPort cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * 用例：查询购物车。
     */
    public CartView queryCart(CartQueryRequest request) {
        return UseCases.execute("查询购物车", () -> {
            Cart cart = cartRepository.findById(CartId.of(request.cartId()))
                    .orElseThrow(() -> new DomainException("购物车不存在：" + request.cartId()));
            return cartAssembler.toCartView(cart);
        });
    }
}