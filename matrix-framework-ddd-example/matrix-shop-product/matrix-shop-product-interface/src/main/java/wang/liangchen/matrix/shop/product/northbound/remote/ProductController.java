package wang.liangchen.matrix.shop.product.northbound.remote;

import org.springframework.web.bind.annotation.*;
import wang.liangchen.matrix.framework.ddd.northbound.remote.IControllerRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;
import wang.liangchen.matrix.shop.product.message.request.*;
import wang.liangchen.matrix.shop.product.message.response.*;
import wang.liangchen.matrix.shop.product.northbound.local.ProductCommandApplicationService;
import wang.liangchen.matrix.shop.product.northbound.local.ProductQueryApplicationService;

import java.util.List;

/**
 * 商品控制器：面向UI的北向远程服务，只操作消息契约，
 * 通过应用服务完成用例编排，不直接访问领域对象。
 */
@RestController
@RequestMapping("/products")
@Remote(RemoteType.Controller)
public class ProductController implements IControllerRemote {

    private final ProductCommandApplicationService commandService;
    private final ProductQueryApplicationService queryService;

    public ProductController(ProductCommandApplicationService commandService, ProductQueryApplicationService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    public CreateProductResult create(@RequestBody CreateProductCommandRequest request) {
        return commandService.createProduct(request);
    }

    @PostMapping("/{productId}/on-sale")
    public PutProductOnSaleResult putOnSale(@PathVariable String productId) {
        return commandService.putProductOnSale(new PutProductOnSaleCommandRequest(productId));
    }

    @PostMapping("/{productId}/off-sale")
    public TakeProductOffSaleResult takeOffSale(@PathVariable String productId) {
        return commandService.takeProductOffSale(new TakeProductOffSaleCommandRequest(productId));
    }

    @PostMapping("/{productId}/skus/{skuId}/price")
    public ChangeSkuPriceResult changeSkuPrice(@PathVariable String productId, @PathVariable String skuId,
                                               @RequestBody ChangeSkuPriceCommandRequest request) {
        return commandService.changeSkuPrice(new ChangeSkuPriceCommandRequest(productId, skuId, request.price()));
    }

    @PostMapping("/{productId}/skus/{skuId}/stock")
    public AdjustSkuStockResult adjustSkuStock(@PathVariable String productId, @PathVariable String skuId,
                                               @RequestBody AdjustSkuStockCommandRequest request) {
        return commandService.adjustSkuStock(new AdjustSkuStockCommandRequest(productId, skuId, request.quantityChange()));
    }

    @GetMapping("/{productId}")
    public ProductDetailView detail(@PathVariable String productId) {
        return queryService.queryProductDetail(new ProductQueryRequest(productId));
    }

    @GetMapping
    public List<ProductView> list(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String categoryId) {
        return queryService.queryProducts(new QueryProductsQueryRequest(keyword, categoryId));
    }
}
