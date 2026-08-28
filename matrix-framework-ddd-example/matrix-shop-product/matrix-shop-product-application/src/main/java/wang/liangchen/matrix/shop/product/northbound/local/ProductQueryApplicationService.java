package wang.liangchen.matrix.shop.product.northbound.local;

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;
import wang.liangchen.matrix.shop.product.domain.port.ProductRepositoryPort;
import wang.liangchen.matrix.shop.product.domain.product.Product;
import wang.liangchen.matrix.shop.product.domain.product.ProductId;
import wang.liangchen.matrix.shop.product.message.request.ProductQueryRequest;
import wang.liangchen.matrix.shop.product.message.request.QueryProductsQueryRequest;
import wang.liangchen.matrix.shop.product.message.response.ProductDetailView;
import wang.liangchen.matrix.shop.product.message.response.ProductView;
import wang.liangchen.matrix.shop.product.northbound.assembler.ProductAssembler;
import wang.liangchen.matrix.shop.product.service.ProductQueryService;

import java.util.List;

/**
 * 商品查询应用服务：CQRS查询侧，经商品仓储端口只读获取商品聚合，
 * 经商品装配器将聚合装配为查询视图，不经领域模型变更路径。
 * 同时是 contract 中应用服务接口 ProductQueryService 的本地实现（单体形态），
 * 微服务形态由 client 模块的 ProductFeignClientAdapter 远程实现。
 */
@Service
@ApplicationService(ApplicationServiceType.QUERY)
public class ProductQueryApplicationService implements IQueryApplicationService, ProductQueryService {

    private final ProductRepositoryPort productRepository;
    private final ProductAssembler productAssembler = new ProductAssembler();

    public ProductQueryApplicationService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 用例：查询商品明细。
     */
    public ProductDetailView queryProductDetail(ProductQueryRequest request) {
        return UseCases.execute("查询商品明细", () -> {
            Product product = productRepository.findById(ProductId.of(request.productId()))
                    .orElseThrow(() -> new DomainException("商品不存在：" + request.productId()));
            return productAssembler.toProductDetailView(product);
        });
    }

    /**
     * 用例：查询商品列表。
     */
    public List<ProductView> queryProducts(QueryProductsQueryRequest request) {
        return UseCases.execute("查询商品列表", () -> {
            CategoryId categoryId = request.categoryId() == null ? null : CategoryId.of(request.categoryId());
            return productRepository.findListed(request.keyword(), categoryId).stream()
                    .map(productAssembler::toProductView)
                    .toList();
        });
    }
}
