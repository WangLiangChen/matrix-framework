package wang.liangchen.matrix.shop.order.southbound.adapter.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IClientAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.order.domain.exception.DomainException;
import wang.liangchen.matrix.shop.order.domain.order.Money;
import wang.liangchen.matrix.shop.order.domain.order.ProductId;
import wang.liangchen.matrix.shop.order.domain.port.ProductClientPort;
import wang.liangchen.matrix.shop.order.domain.readmodel.ProductSummary;
import wang.liangchen.matrix.shop.product.message.response.ProductDetailView;
import wang.liangchen.matrix.shop.product.message.response.SkuView;

import java.math.BigDecimal;

/**
 * 商品客户端适配器：订单上下文对商品上下文（上游）的防腐层实现。
 * 调用商品上下文的开放主机服务（Resource），将商品上下文的发布语言
 * （ProductDetailView）翻译为订单领域的商品摘要（ProductSummary），
 * 商品上下文的外部模型不得腐化订单领域模型；服务不可用等基础设施异常
 * 在适配器内转换为领域语义的DomainException。
 */
@Component
@Adapter(PortType.Client)
public class ProductClientAdapter implements ProductClientPort, IClientAdapter {

    private final RestClient restClient;

    public ProductClientAdapter(@Value("${matrix.shop.order.product-service-url:http://localhost:8081}") String productServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(productServiceUrl).build();
    }

    @Override
    public ProductSummary obtainProduct(ProductId productId) {
        try {
            ProductDetailView view = restClient.get()
                    .uri("/product-resources/products/{productId}", productId.value())
                    .retrieve()
                    .body(ProductDetailView.class);
            if (view == null || !view.listed()) {
                throw new DomainException("商品不存在或已下架：" + productId.value());
            }
            BigDecimal minPrice = view.skus().stream()
                    .map(SkuView::price)
                    .min(BigDecimal::compareTo)
                    .orElseThrow(() -> new DomainException("商品无可用SKU：" + productId.value()));
            return new ProductSummary(ProductId.of(view.id()), view.name(), Money.CNY(minPrice));
        } catch (AbstractDomainException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new DomainException("商品服务不可用，无法获取商品信息：" + productId.value(), ex);
        }
    }
}
