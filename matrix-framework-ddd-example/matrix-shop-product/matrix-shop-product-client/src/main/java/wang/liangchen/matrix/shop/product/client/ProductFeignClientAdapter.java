package wang.liangchen.matrix.shop.product.client;

import wang.liangchen.matrix.shop.product.message.request.ProductQueryRequest;
import wang.liangchen.matrix.shop.product.message.response.ProductDetailView;
import wang.liangchen.matrix.shop.product.service.ProductQueryService;

/**
 * 商品客户端适配器：商品上下文客户端 SDK 的远程实现，实现 contract 中的应用服务接口
 * （ProductQueryService），在方法中执行远程调用并转换参数与返回结果。
 * 远程调用失败抛出的技术异常由调用方的防腐层翻译，本模块不感知商品上下文的
 * 领域模型与内部实现；类为普通 Java 类，由调用方自行装配（如以 @Bean 注册）。
 */
public class ProductFeignClientAdapter implements ProductQueryService {

    private final ProductFeignClient productFeignClient;

    public ProductFeignClientAdapter(ProductFeignClient productFeignClient) {
        this.productFeignClient = productFeignClient;
    }

    @Override
    public ProductDetailView queryProductDetail(ProductQueryRequest request) {
        return productFeignClient.product(request.productId());
    }
}
