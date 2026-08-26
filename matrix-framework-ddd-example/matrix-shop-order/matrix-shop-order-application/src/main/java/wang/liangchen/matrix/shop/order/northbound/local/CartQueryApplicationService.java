package wang.liangchen.matrix.shop.order.northbound.local;

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;
import wang.liangchen.matrix.shop.order.domain.cart.CartId;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.domain.port.CartQueryPort;
import wang.liangchen.matrix.shop.order.domain.readmodel.CartDetail;
import wang.liangchen.matrix.shop.order.domain.readmodel.CartItemSummary;
import wang.liangchen.matrix.shop.order.message.request.CartQueryRequest;
import wang.liangchen.matrix.shop.order.message.response.CartItemView;
import wang.liangchen.matrix.shop.order.message.response.CartView;
import wang.liangchen.matrix.shop.order.northbound.exception.ApplicationException;

import java.util.function.Supplier;

/**
 * 购物车查询应用服务：CQRS查询侧，只读访问购物车读模型。
 */
@Service
@ApplicationService(ApplicationServiceType.QUERY)
public class CartQueryApplicationService implements IQueryApplicationService {

    private final CartQueryPort cartQuery;

    public CartQueryApplicationService(CartQueryPort cartQuery) {
        this.cartQuery = cartQuery;
    }

    /**
     * 用例：查询购物车。
     */
    public CartView queryCart(CartQueryRequest request) {
        return useCase("查询购物车", () -> {
            CartDetail detail = cartQuery.queryById(CartId.of(request.cartId()))
                    .orElseThrow(() -> new DomainException("购物车不存在：" + request.cartId()));
            return new CartView(detail.id().value(), detail.buyerId().value(),
                    detail.items().stream().map(this::cartItemView).toList(),
                    detail.totalAmount().amount());
        });
    }

    private CartItemView cartItemView(CartItemSummary item) {
        return new CartItemView(item.productId().value(), item.productName(),
                item.unitPrice().amount(), item.quantity(), item.unitPrice().multiply(item.quantity()).amount());
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
