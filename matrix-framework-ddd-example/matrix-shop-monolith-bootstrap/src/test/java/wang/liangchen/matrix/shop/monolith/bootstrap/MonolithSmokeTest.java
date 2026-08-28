package wang.liangchen.matrix.shop.monolith.bootstrap;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 单体端到端冒烟测试：在一个事务性HTTP流程中覆盖
 * 商品上下文（创建/上架/下架）与订单上下文（加购/下单）的跨上下文协作——
 * ①下单后清空购物车（同上下文领域事件订阅闭环）；
 * ②商品下架后从购物车移除该商品（跨上下文契约事件订阅）；
 * ③订阅者远程入口（HTTP端点接收事件契约）；
 * ④订单定价领域服务（大宗+忠诚折扣）接入下单用例；
 * ⑤调度场景（超时未支付订单自动取消，经调度器手动触发端点验证）。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MonolithSmokeTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    @DisplayName("全链路：创建商品→加购→下单（清空购物车+折扣定价）→事件订阅者移除购物车商品")
    void fullJourney() {
        // 创建商品（SPU+SKU）并上架
        Map<String, Object> sku = Map.of("attributeValues", List.of(), "price", 100.00, "stock", 100);
        Map<String, Object> createProduct = Map.of(
                "name", "智能手机", "subtitle", "旗舰机型",
                "categoryId", "category-1", "brandId", "brand-1",
                "attributeValues", List.of(), "skus", List.of(sku));
        ResponseEntity<JsonNode> created = rest.postForEntity("/products", createProduct, JsonNode.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
        String productId = created.getBody().path("productId").asText();
        assertThat(productId).isNotBlank();

        ResponseEntity<JsonNode> onSale = rest.postForEntity("/products/{id}/on-sale", null, JsonNode.class, productId);
        assertThat(onSale.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 加购（防腐层经开放主机服务获取商品快照）
        ResponseEntity<JsonNode> added = rest.postForEntity("/carts/cart-1/items",
                Map.of("buyerId", "buyer-1", "productId", productId, "quantity", 2), JsonNode.class);
        assertThat(added.getStatusCode()).isEqualTo(HttpStatus.OK);

        JsonNode cart = cartDetail("cart-1");
        assertThat(cart.path("items")).hasSize(1);
        assertThat(cart.path("totalAmount").decimalValue()).isEqualByComparingTo("200.00");

        // 下单（金卡买家：200.00×0.98=196.00）
        Map<String, Object> receiver = Map.of("receiver", "张三", "phone", "13800000000",
                "province", "上海", "city", "上海", "detail", "某路1号");
        Map<String, Object> placeOrder = Map.of(
                "buyerId", "buyer-1", "receiver", receiver,
                "items", List.of(Map.of("productId", productId, "quantity", 2)),
                "loyaltyLevel", "GOLD");
        ResponseEntity<JsonNode> order = rest.postForEntity("/orders", placeOrder, JsonNode.class);
        assertThat(order.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(order.getBody().path("totalAmount").decimalValue()).isEqualByComparingTo("196.00");
        String orderId = order.getBody().path("orderId").asText();

        // 同上下文领域事件订阅闭环：下单事务提交后购物车已清空
        JsonNode clearedCart = cartDetail("cart-1");
        assertThat(clearedCart.path("items")).isEmpty();
        assertThat(clearedCart.path("totalAmount").decimalValue()).isEqualByComparingTo("0.00");

        // 订单可查询
        ResponseEntity<JsonNode> orderDetail = rest.getForEntity("/orders/{id}", JsonNode.class, orderId);
        assertThat(orderDetail.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(orderDetail.getBody().path("status").asText()).isEqualTo("Created");

        // 订阅者远程入口：事件契约经HTTP端点投递，购物车商品被移除
        addCartItem("cart-2", "buyer-2", productId, 1);
        ResponseEntity<Void> delivered = rest.postForEntity("/order-subscribers/product-delisted",
                Map.of("eventId", UUID.randomUUID().toString(),
                        "occurredOn", Instant.now().toString(),
                        "productId", productId), Void.class);
        assertThat(delivered.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode cart2 = cartDetail("cart-2");
        assertThat(cart2.path("items")).isEmpty();

        // 跨上下文契约事件订阅：商品下架（进程内事件总线）→包含该商品的购物车移除该商品
        addCartItem("cart-3", "buyer-3", productId, 1);
        ResponseEntity<JsonNode> offSale = rest.postForEntity("/products/{id}/off-sale", null, JsonNode.class, productId);
        assertThat(offSale.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(offSale.getBody().path("listed").asBoolean()).isFalse();
        JsonNode cart3 = cartDetail("cart-3");
        assertThat(cart3.path("items")).isEmpty();
    }

    @Test
    @DisplayName("调度场景：超时未支付订单经调度器远程入口手动触发自动取消")
    void timeoutUnpaidOrderCanceledByScheduler() {
        // 创建商品（SPU+SKU）并上架
        Map<String, Object> sku = Map.of("attributeValues", List.of(), "price", 50.00, "stock", 10);
        Map<String, Object> createProduct = Map.of(
                "name", "蓝牙耳机", "subtitle", "入耳式",
                "categoryId", "category-1", "brandId", "brand-1",
                "attributeValues", List.of(), "skus", List.of(sku));
        ResponseEntity<JsonNode> created = rest.postForEntity("/products", createProduct, JsonNode.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
        String productId = created.getBody().path("productId").asText();

        ResponseEntity<JsonNode> onSale = rest.postForEntity("/products/{id}/on-sale", null, JsonNode.class, productId);
        assertThat(onSale.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 买家下单后未支付（待支付状态）
        String orderId = placeOrder("buyer-4", productId, 1, "GOLD");
        assertThat(orderStatus(orderId)).isEqualTo("Created");

        // 阈值30分钟：刚下的订单未超时，不会被取消
        ResponseEntity<JsonNode> keep = rest.postForEntity("/order-schedulers/cancel-timeout-orders",
                Map.of("timeoutMinutes", 30), JsonNode.class);
        assertThat(keep.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(orderStatus(orderId)).isEqualTo("Created");

        // 阈值0分钟：订单已超时，经调度器手动触发端点自动取消
        ResponseEntity<JsonNode> cancel = rest.postForEntity("/order-schedulers/cancel-timeout-orders",
                Map.of("timeoutMinutes", 0), JsonNode.class);
        assertThat(cancel.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cancel.getBody().path("canceledCount").asInt()).isGreaterThanOrEqualTo(1);
        assertThat(orderStatus(orderId)).isEqualTo("Canceled");
    }

    private void addCartItem(String cartId, String buyerId, String productId, int quantity) {
        ResponseEntity<JsonNode> response = rest.postForEntity("/carts/{cartId}/items",
                Map.of("buyerId", buyerId, "productId", productId, "quantity", quantity), JsonNode.class, cartId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private JsonNode cartDetail(String cartId) {
        ResponseEntity<JsonNode> response = rest.getForEntity("/carts/{cartId}", JsonNode.class, cartId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private String placeOrder(String buyerId, String productId, int quantity, String loyaltyLevel) {
        Map<String, Object> receiver = Map.of("receiver", "张三", "phone", "13800000000",
                "province", "上海", "city", "上海", "detail", "某路1号");
        Map<String, Object> placeOrder = Map.of(
                "buyerId", buyerId, "receiver", receiver,
                "items", List.of(Map.of("productId", productId, "quantity", quantity)),
                "loyaltyLevel", loyaltyLevel);
        ResponseEntity<JsonNode> order = rest.postForEntity("/orders", placeOrder, JsonNode.class);
        assertThat(order.getStatusCode()).isEqualTo(HttpStatus.OK);
        return order.getBody().path("orderId").asText();
    }

    private String orderStatus(String orderId) {
        ResponseEntity<JsonNode> orderDetail = rest.getForEntity("/orders/{id}", JsonNode.class, orderId);
        assertThat(orderDetail.getStatusCode()).isEqualTo(HttpStatus.OK);
        return orderDetail.getBody().path("status").asText();
    }
}
