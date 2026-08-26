package wang.liangchen.matrix.shop.product.northbound.local;

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;
import wang.liangchen.matrix.shop.product.domain.port.ProductQueryPort;
import wang.liangchen.matrix.shop.product.domain.product.ProductId;
import wang.liangchen.matrix.shop.product.domain.readmodel.ProductDetail;
import wang.liangchen.matrix.shop.product.domain.readmodel.ProductSummary;
import wang.liangchen.matrix.shop.product.message.request.AttributeValue;
import wang.liangchen.matrix.shop.product.message.request.ProductQueryRequest;
import wang.liangchen.matrix.shop.product.message.request.QueryProductsQueryRequest;
import wang.liangchen.matrix.shop.product.message.response.ProductDetailView;
import wang.liangchen.matrix.shop.product.message.response.ProductView;
import wang.liangchen.matrix.shop.product.message.response.SkuView;
import wang.liangchen.matrix.shop.product.northbound.exception.ApplicationException;

import java.util.List;
import java.util.function.Supplier;

/**
 * 商品查询应用服务：CQRS查询侧，只读访问商品读模型，
 * 不经领域模型变更路径，将读模型装配为查询视图。
 */
@Service
@ApplicationService(ApplicationServiceType.QUERY)
public class ProductQueryApplicationService implements IQueryApplicationService {

    private final ProductQueryPort productQuery;

    public ProductQueryApplicationService(ProductQueryPort productQuery) {
        this.productQuery = productQuery;
    }

    /**
     * 用例：查询商品明细。
     */
    public ProductDetailView queryProductDetail(ProductQueryRequest request) {
        return useCase("查询商品明细", () -> {
            ProductDetail detail = productQuery.queryById(ProductId.of(request.productId()))
                    .orElseThrow(() -> new DomainException("商品不存在：" + request.productId()));
            return new ProductDetailView(detail.id().value(), detail.name(), detail.subtitle(),
                    detail.categoryId().value(), detail.brandId().value(), detail.listed(),
                    attributeValues(detail.attributeValues()),
                    detail.skus().stream().map(this::skuView).toList());
        });
    }

    /**
     * 用例：查询商品列表。
     */
    public List<ProductView> queryProducts(QueryProductsQueryRequest request) {
        return useCase("查询商品列表", () -> {
            CategoryId categoryId = request.categoryId() == null ? null : CategoryId.of(request.categoryId());
            List<ProductSummary> summaries = productQuery.queryProducts(request.keyword(), categoryId);
            return summaries.stream().map(this::productView).toList();
        });
    }

    private ProductView productView(ProductSummary summary) {
        return new ProductView(summary.id().value(), summary.name(), summary.subtitle(),
                summary.categoryId().value(), summary.brandId().value(), summary.listed());
    }

    private SkuView skuView(wang.liangchen.matrix.shop.product.domain.readmodel.SkuSummary sku) {
        return new SkuView(sku.id().value(), attributeValues(sku.attributeValues()), sku.price().amount(), sku.stock());
    }

    private List<AttributeValue> attributeValues(List<wang.liangchen.matrix.shop.product.domain.product.AttributeValueRef> refs) {
        return refs.stream()
                .map(ref -> new AttributeValue(ref.attributeId().value(), ref.value()))
                .toList();
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
