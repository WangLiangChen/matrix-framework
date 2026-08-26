package wang.liangchen.matrix.shop.product.northbound.remote;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.liangchen.matrix.framework.ddd.northbound.remote.IResourceRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;
import wang.liangchen.matrix.shop.product.message.request.ProductQueryRequest;
import wang.liangchen.matrix.shop.product.message.response.ProductDetailView;
import wang.liangchen.matrix.shop.product.northbound.local.ProductQueryApplicationService;

/**
 * 商品资源：面向下游限界上下文的开放主机服务（客户-供应商模式中的上游），
 * 以商品上下文的发布语言（ProductDetailView）服务下游，
 * 订单上下文的防腐层通过本资源获取商品快照。
 */
@RestController
@RequestMapping("/product-resources")
@Remote(RemoteType.Resource)
public class ProductResource implements IResourceRemote {

    private final ProductQueryApplicationService queryService;

    public ProductResource(ProductQueryApplicationService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/products/{productId}")
    public ProductDetailView product(@PathVariable String productId) {
        return queryService.queryProductDetail(new ProductQueryRequest(productId));
    }
}
