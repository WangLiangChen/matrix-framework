package wang.liangchen.matrix.shop.product.client;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import wang.liangchen.matrix.shop.product.message.response.ProductDetailView;

/**
 * 商品微服务远程客户端：调用独立部署的商品上下文开放主机服务（ProductResource）。
 * 命名沿用 Feign 语义（声明式远程调用），传输以 Spring RestClient 实现，
 * 未引入 Spring Cloud，保持技术栈最小。
 */
public class ProductFeignClient {

    private final RestClient restClient;

    public ProductFeignClient(String productServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(productServiceUrl).build();
    }

    /**
     * 查询商品明细（GET /product-resources/products/{productId}）。
     */
    public ProductDetailView product(String productId) {
        ProductDetailView view = restClient.get()
                .uri("/product-resources/products/{productId}", productId)
                .retrieve()
                .body(ProductDetailView.class);
        if (view == null) {
            throw new RestClientException("商品服务返回空响应：" + productId);
        }
        return view;
    }
}
