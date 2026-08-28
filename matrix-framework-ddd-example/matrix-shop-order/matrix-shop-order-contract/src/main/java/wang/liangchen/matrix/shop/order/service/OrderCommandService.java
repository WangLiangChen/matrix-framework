package wang.liangchen.matrix.shop.order.service;

import wang.liangchen.matrix.shop.order.message.request.CancelOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.request.CompleteOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.request.PayOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.request.PlaceOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.request.ShipOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.response.CancelOrderResult;
import wang.liangchen.matrix.shop.order.message.response.CompleteOrderResult;
import wang.liangchen.matrix.shop.order.message.response.PayOrderResult;
import wang.liangchen.matrix.shop.order.message.response.PlaceOrderResult;
import wang.liangchen.matrix.shop.order.message.response.ShipOrderResult;

/**
 * 订单命令服务接口：订单上下文面向下游消费者的命令侧应用服务契约（发布语言的接口形式），
 * 方法只操作消息契约，不暴露领域模型。
 * 单体部署时由应用服务本地实现（northbound.local），微服务部署时由 client 模块的
 * 远程适配器实现，下游只依赖本接口（按需引入 client）即可不感知部署形态。
 */
public interface OrderCommandService {

    /**
     * 用例：下单。
     */
    PlaceOrderResult placeOrder(PlaceOrderCommandRequest request);

    /**
     * 用例：支付订单。
     */
    PayOrderResult payOrder(PayOrderCommandRequest request);

    /**
     * 用例：发货。
     */
    ShipOrderResult shipOrder(ShipOrderCommandRequest request);

    /**
     * 用例：完成订单。
     */
    CompleteOrderResult completeOrder(CompleteOrderCommandRequest request);

    /**
     * 用例：取消订单。
     */
    CancelOrderResult cancelOrder(CancelOrderCommandRequest request);
}
