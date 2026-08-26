package wang.liangchen.matrix.shop.product.client;

/**
 * 1、注入ProductFeignClient，用于调用独立部署的微服务
 * 2、实现contract中的应用服务接口，在方法中执行远程调用，并转换参数和返回结果。
 */
public class ProductFeignClientAdapter {
    private final ProductFeignClient productFeignClient;

    public ProductFeignClientAdapter(ProductFeignClient productFeignClient) {
        this.productFeignClient = productFeignClient;
    }

}
