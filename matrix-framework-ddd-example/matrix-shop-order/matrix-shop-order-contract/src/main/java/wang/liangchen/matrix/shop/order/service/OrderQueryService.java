package wang.liangchen.matrix.shop.order.service;

import wang.liangchen.matrix.shop.order.message.request.OrderQueryRequest;
import wang.liangchen.matrix.shop.order.message.request.QueryOrdersQueryRequest;
import wang.liangchen.matrix.shop.order.message.response.OrderDetailView;
import wang.liangchen.matrix.shop.order.message.response.OrderView;

import java.util.List;

/**
 * 订单查询服务接口：订单上下文面向下游消费者的查询侧应用服务契约（发布语言的接口形式），
 * 方法只操作消息契约，不暴露领域模型。
 * 单体部署时由应用服务本地实现（northbound.local），微服务部署时由 client 模块的
 * 远程适配器实现，下游只依赖本接口（按需引入 client）即可不感知部署形态。
 */
public interface OrderQueryService {

    /**
     * 用例：查询订单明细。
     */
    OrderDetailView queryOrder(OrderQueryRequest request);

    /**
     * 用例：查询买家订单列表。
     */
    List<OrderView> queryOrders(QueryOrdersQueryRequest request);
}
