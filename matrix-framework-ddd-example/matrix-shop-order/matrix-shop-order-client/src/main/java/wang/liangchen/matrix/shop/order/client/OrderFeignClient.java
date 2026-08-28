package wang.liangchen.matrix.shop.order.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import wang.liangchen.matrix.shop.order.message.request.PlaceOrderCommandRequest;
import wang.liangchen.matrix.shop.order.message.response.CancelOrderResult;
import wang.liangchen.matrix.shop.order.message.response.CompleteOrderResult;
import wang.liangchen.matrix.shop.order.message.response.OrderDetailView;
import wang.liangchen.matrix.shop.order.message.response.OrderView;
import wang.liangchen.matrix.shop.order.message.response.PayOrderResult;
import wang.liangchen.matrix.shop.order.message.response.PlaceOrderResult;
import wang.liangchen.matrix.shop.order.message.response.ShipOrderResult;

import java.util.List;

/**
 * 订单微服务远程客户端：调用独立部署的订单上下文北向远程服务（OrderController）。
 * 命名沿用 Feign 语义（声明式远程调用），传输以 Spring RestClient 实现，
 * 未引入 Spring Cloud，保持技术栈最小。
 */
public class OrderFeignClient {

    private final RestClient restClient;

    public OrderFeignClient(String orderServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(orderServiceUrl).build();
    }

    /**
     * 下单（POST /orders）。
     */
    public PlaceOrderResult place(PlaceOrderCommandRequest request) {
        return requireBody(restClient.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PlaceOrderResult.class), "下单");
    }

    /**
     * 支付订单（POST /orders/{orderId}/pay）。
     */
    public PayOrderResult pay(String orderId) {
        return requireBody(restClient.post()
                .uri("/orders/{orderId}/pay", orderId)
                .retrieve()
                .body(PayOrderResult.class), "支付订单");
    }

    /**
     * 发货（POST /orders/{orderId}/ship）。
     */
    public ShipOrderResult ship(String orderId) {
        return requireBody(restClient.post()
                .uri("/orders/{orderId}/ship", orderId)
                .retrieve()
                .body(ShipOrderResult.class), "发货");
    }

    /**
     * 完成订单（POST /orders/{orderId}/complete）。
     */
    public CompleteOrderResult complete(String orderId) {
        return requireBody(restClient.post()
                .uri("/orders/{orderId}/complete", orderId)
                .retrieve()
                .body(CompleteOrderResult.class), "完成订单");
    }

    /**
     * 取消订单（POST /orders/{orderId}/cancel）。
     */
    public CancelOrderResult cancel(String orderId) {
        return requireBody(restClient.post()
                .uri("/orders/{orderId}/cancel", orderId)
                .retrieve()
                .body(CancelOrderResult.class), "取消订单");
    }

    /**
     * 查询订单明细（GET /orders/{orderId}）。
     */
    public OrderDetailView detail(String orderId) {
        return requireBody(restClient.get()
                .uri("/orders/{orderId}", orderId)
                .retrieve()
                .body(OrderDetailView.class), "查询订单明细");
    }

    /**
     * 查询买家订单列表（GET /orders?buyerId={buyerId}）。
     */
    public List<OrderView> list(String buyerId) {
        List<OrderView> views = restClient.get()
                .uri("/orders?buyerId={buyerId}", buyerId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<OrderView>>() {
                });
        if (views == null) {
            throw new RestClientException("订单服务返回空响应：查询订单列表");
        }
        return views;
    }

    private <T> T requireBody(T body, String useCaseName) {
        if (body == null) {
            throw new RestClientException("订单服务返回空响应：" + useCaseName);
        }
        return body;
    }
}
