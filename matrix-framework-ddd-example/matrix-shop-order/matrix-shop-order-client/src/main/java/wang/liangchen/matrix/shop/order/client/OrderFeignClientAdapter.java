package wang.liangchen.matrix.shop.order.client;

import wang.liangchen.matrix.shop.order.message.request.CancelOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.request.CompleteOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.request.OrderQueryRequest;
import wang.liangchen.matrix.shop.order.message.request.PayOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.request.PlaceOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.request.QueryOrdersQueryRequest;
import wang.liangchen.matrix.shop.order.message.request.ShipOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.response.CancelOrderResult;
import wang.liangchen.matrix.shop.order.message.response.CompleteOrderResult;
import wang.liangchen.matrix.shop.order.message.response.OrderDetailView;
import wang.liangchen.matrix.shop.order.message.response.OrderView;
import wang.liangchen.matrix.shop.order.message.response.PayOrderResult;
import wang.liangchen.matrix.shop.order.message.response.PlaceOrderResult;
import wang.liangchen.matrix.shop.order.message.response.ShipOrderResult;
import wang.liangchen.matrix.shop.order.service.OrderCommandService;
import wang.liangchen.matrix.shop.order.service.OrderQueryService;

import java.util.List;

/**
 * 订单客户端适配器：订单上下文客户端 SDK 的远程实现，实现 contract 中的应用服务接口
 * （OrderCommandService/OrderQueryService），在方法中执行远程调用并转换参数与返回结果。
 * 远程调用失败抛出的技术异常由调用方的防腐层翻译，本模块不感知订单上下文的
 * 领域模型与内部实现；类为普通 Java 类，由调用方自行装配（如以 @Bean 注册）。
 */
public class OrderFeignClientAdapter implements OrderCommandService, OrderQueryService {

    private final OrderFeignClient orderFeignClient;

    public OrderFeignClientAdapter(OrderFeignClient orderFeignClient) {
        this.orderFeignClient = orderFeignClient;
    }

    @Override
    public PlaceOrderResult placeOrder(PlaceOrderCommandRequest request) {
        return orderFeignClient.place(request);
    }

    @Override
    public PayOrderResult payOrder(PayOrderCommandRequest request) {
        return orderFeignClient.pay(request.orderId());
    }

    @Override
    public ShipOrderResult shipOrder(ShipOrderCommandRequest request) {
        return orderFeignClient.ship(request.orderId());
    }

    @Override
    public CompleteOrderResult completeOrder(CompleteOrderCommandRequest request) {
        return orderFeignClient.complete(request.orderId());
    }

    @Override
    public CancelOrderResult cancelOrder(CancelOrderCommandRequest request) {
        return orderFeignClient.cancel(request.orderId());
    }

    @Override
    public OrderDetailView queryOrder(OrderQueryRequest request) {
        return orderFeignClient.detail(request.orderId());
    }

    @Override
    public List<OrderView> queryOrders(QueryOrdersQueryRequest request) {
        return orderFeignClient.list(request.buyerId());
    }
}
