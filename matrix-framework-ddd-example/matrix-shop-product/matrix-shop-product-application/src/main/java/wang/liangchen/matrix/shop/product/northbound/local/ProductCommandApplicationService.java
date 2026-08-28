package wang.liangchen.matrix.shop.product.northbound.local;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.domain.event.IDomainEvent;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.ICommandApplicationService;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeId;
import wang.liangchen.matrix.shop.product.domain.brand.BrandId;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;
import wang.liangchen.matrix.shop.product.domain.port.DomainEventPublisherPort;
import wang.liangchen.matrix.shop.product.domain.port.ProductRepositoryPort;
import wang.liangchen.matrix.shop.product.domain.product.*;
import wang.liangchen.matrix.shop.product.message.event.ProductDelistedEvent;
import wang.liangchen.matrix.shop.product.message.request.*;
import wang.liangchen.matrix.shop.product.message.response.*;
import wang.liangchen.matrix.shop.product.northbound.assembler.ProductEventAssembler;
import wang.liangchen.matrix.shop.product.northbound.exception.ApplicationException;

import java.util.List;
import java.util.function.Consumer;

/**
 * 商品命令应用服务：编排商品聚合实现命令用例，一个用例对应一个事务，
 * 一次事务只修改一个商品聚合实例，领域事件在聚合保存后统一发布；
 * 需要对外发布的领域事件经装配器翻译为事件契约（发布语言）发布，供跨上下文订阅。
 */
@Service
@ApplicationService(ApplicationServiceType.COMMAND)
public class ProductCommandApplicationService implements ICommandApplicationService {

    private final ProductRepositoryPort productRepository;
    private final DomainEventPublisherPort eventPublisher;
    private final ProductEventAssembler productEventAssembler = new ProductEventAssembler();
    private final ProductFactory productFactory = new ProductFactory();

    public ProductCommandApplicationService(ProductRepositoryPort productRepository, DomainEventPublisherPort eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 用例：创建商品。
     */
    @Transactional
    public CreateProductResult createProduct(CreateProductCommandRequest request) {
        return UseCases.execute("创建商品", () -> {
            Product product = productFactory.create(request.name(), request.subtitle(),
                    CategoryId.of(request.categoryId()), BrandId.of(request.brandId()),
                    attributeValues(request.attributeValues()), skuTemplates(request.skus()));
            productRepository.save(product);
            eventPublisher.publish(product.events());
            product.clearEvents();
            return new CreateProductResult(product.id().value());
        });
    }

    /**
     * 用例：商品上架。
     */
    @Transactional
    public PutProductOnSaleResult putProductOnSale(PutProductOnSaleCommandRequest request) {
        return UseCases.execute("商品上架", () -> {
            Product product = mutate(ProductId.of(request.productId()), Product::putOnSale);
            return new PutProductOnSaleResult(product.id().value(), product.listed());
        });
    }

    /**
     * 用例：商品下架。
     * "商品已下架"领域事件经装配器翻译为事件契约对外发布，供下游上下文（如订单上下文）订阅。
     */
    @Transactional
    public TakeProductOffSaleResult takeProductOffSale(TakeProductOffSaleCommandRequest request) {
        return UseCases.execute("商品下架", () -> {
            Product product = productRepository.findById(ProductId.of(request.productId()))
                    .orElseThrow(() -> new DomainException("商品不存在：" + request.productId()));
            product.takeOffSale();
            productRepository.save(product);
            eventPublisher.publish(product.events());
            publishContractEvents(product.events());
            product.clearEvents();
            return new TakeProductOffSaleResult(product.id().value(), product.listed());
        });
    }

    /**
     * 将需要对外发布的领域事件经装配器翻译为事件契约（发布语言）发布。
     */
    private void publishContractEvents(List<IDomainEvent> domainEvents) {
        domainEvents.forEach(domainEvent -> {
            if (domainEvent instanceof ProductDelisted delisted) {
                ProductDelistedEvent contractEvent = productEventAssembler.toContractEvent(delisted);
                eventPublisher.publishContract(contractEvent);
            }
        });
    }

    /**
     * 用例：调整SKU价格。
     */
    @Transactional
    public ChangeSkuPriceResult changeSkuPrice(ChangeSkuPriceCommandRequest request) {
        return UseCases.execute("调整SKU价格", () -> {
            Product product = mutate(ProductId.of(request.productId()),
                    p -> p.changeSkuPrice(SkuId.of(request.skuId()), Money.CNY(request.price())));
            return new ChangeSkuPriceResult(request.productId(), request.skuId(), request.price());
        });
    }

    /**
     * 用例：调整SKU库存。
     */
    @Transactional
    public AdjustSkuStockResult adjustSkuStock(AdjustSkuStockCommandRequest request) {
        return UseCases.execute("调整SKU库存", () -> {
            if (request.quantityChange() == 0) {
                throw new DomainException("库存调整量不能为零");
            }
            mutate(ProductId.of(request.productId()), product -> {
                if (request.quantityChange() > 0) {
                    product.increaseSkuStock(SkuId.of(request.skuId()), request.quantityChange());
                } else {
                    product.decreaseSkuStock(SkuId.of(request.skuId()), -request.quantityChange());
                }
            });
            return new AdjustSkuStockResult(request.productId(), request.skuId(), request.quantityChange());
        });
    }

    /**
     * 查询商品聚合并执行变更，随后保存并发布聚合收集的领域事件。
     */
    private Product mutate(ProductId productId, Consumer<Product> mutation) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DomainException("商品不存在：" + productId.value()));
        mutation.accept(product);
        productRepository.save(product);
        eventPublisher.publish(product.events());
        product.clearEvents();
        return product;
    }

    private List<AttributeValueRef> attributeValues(List<AttributeValue> values) {
        return values.stream()
                .map(value -> new AttributeValueRef(AttributeId.of(value.attributeId()), value.value()))
                .toList();
    }

    private List<SkuTemplate> skuTemplates(List<CreateProductCommandRequest.Sku> skus) {
        return skus.stream()
                .map(sku -> new SkuTemplate(attributeValues(sku.attributeValues()), Money.CNY(sku.price()), sku.stock()))
                .toList();
    }
}
