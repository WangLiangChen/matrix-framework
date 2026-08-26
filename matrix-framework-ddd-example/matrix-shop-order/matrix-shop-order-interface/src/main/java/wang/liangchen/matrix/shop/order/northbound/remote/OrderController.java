package wang.liangchen.matrix.shop.order.northbound.remote;

import org.springframework.web.bind.annotation.*;
import wang.liangchen.matrix.framework.ddd.northbound.remote.IControllerRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;
import wang.liangchen.matrix.shop.order.message.request.*;
import wang.liangchen.matrix.shop.order.message.response.*;
import wang.liangchen.matrix.shop.order.northbound.local.OrderCommandApplicationService;
import wang.liangchen.matrix.shop.order.northbound.local.OrderQueryApplicationService;

import java.util.List;

/**
 * 订单控制器：面向UI的北向远程服务，只操作消息契约，
 * 通过应用服务完成用例编排，不直接访问领域对象。
 */
@RestController
@RequestMapping("/orders")
@Remote(RemoteType.Controller)
public class OrderController implements IControllerRemote {

    private final OrderCommandApplicationService commandService;
    private final OrderQueryApplicationService queryService;

    public OrderController(OrderCommandApplicationService commandService, OrderQueryApplicationService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    public PlaceOrderResult place(@RequestBody PlaceOrderCommandRequest request) {
        return commandService.placeOrder(request);
    }

    @PostMapping("/{orderId}/pay")
    public PayOrderResult pay(@PathVariable String orderId) {
        return commandService.payOrder(new PayOrderCommandRequest(orderId));
    }

    @PostMapping("/{orderId}/ship")
    public ShipOrderResult ship(@PathVariable String orderId) {
        return commandService.shipOrder(new ShipOrderCommandRequest(orderId));
    }

    @PostMapping("/{orderId}/complete")
    public CompleteOrderResult complete(@PathVariable String orderId) {
        return commandService.completeOrder(new CompleteOrderCommandRequest(orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public CancelOrderResult cancel(@PathVariable String orderId) {
        return commandService.cancelOrder(new CancelOrderCommandRequest(orderId));
    }

    @GetMapping("/{orderId}")
    public OrderDetailView detail(@PathVariable String orderId) {
        return queryService.queryOrder(new OrderQueryRequest(orderId));
    }

    @GetMapping
    public List<OrderView> list(@RequestParam String buyerId) {
        return queryService.queryOrders(new QueryOrdersQueryRequest(buyerId));
    }
}
