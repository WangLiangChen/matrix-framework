package wang.liangchen.matrix.shop.order.northbound.remote;

import org.springframework.web.bind.annotation.*;
import wang.liangchen.matrix.framework.ddd.northbound.remote.IControllerRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;
import wang.liangchen.matrix.shop.order.message.request.*;
import wang.liangchen.matrix.shop.order.message.response.*;
import wang.liangchen.matrix.shop.order.northbound.local.CartCommandApplicationService;
import wang.liangchen.matrix.shop.order.northbound.local.CartQueryApplicationService;

/**
 * 购物车控制器：面向UI的北向远程服务，只操作消息契约。
 */
@RestController
@RequestMapping("/carts")
@Remote(RemoteType.Controller)
public class CartController implements IControllerRemote {

    private final CartCommandApplicationService commandService;
    private final CartQueryApplicationService queryService;

    public CartController(CartCommandApplicationService commandService, CartQueryApplicationService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping("/{cartId}/items")
    public AddCartItemResult addItem(@PathVariable String cartId, @RequestBody AddCartItemCommandRequest request) {
        return commandService.addCartItem(new AddCartItemCommandRequest(cartId, request.buyerId(), request.productId(), request.quantity()));
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ChangeCartItemQuantityResult changeQuantity(@PathVariable String cartId, @PathVariable String productId,
                                                       @RequestBody ChangeCartItemQuantityCommandRequest request) {
        return commandService.changeCartItemQuantity(new ChangeCartItemQuantityCommandRequest(cartId, productId, request.quantity()));
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public RemoveCartItemResult removeItem(@PathVariable String cartId, @PathVariable String productId) {
        return commandService.removeCartItem(new RemoveCartItemCommandRequest(cartId, productId));
    }

    @DeleteMapping("/{cartId}")
    public ClearCartResult clear(@PathVariable String cartId) {
        return commandService.clearCart(new ClearCartCommandRequest(cartId));
    }

    @GetMapping("/{cartId}")
    public CartView detail(@PathVariable String cartId) {
        return queryService.queryCart(new CartQueryRequest(cartId));
    }
}
