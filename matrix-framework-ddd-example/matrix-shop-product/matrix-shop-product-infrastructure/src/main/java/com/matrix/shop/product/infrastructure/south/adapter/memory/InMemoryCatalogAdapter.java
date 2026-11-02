package wang.liangchen.matrix.product.infrastructure.south.adapter.memory;

import wang.liangchen.matrix.shop.product.domain.aggregate.attribute.Attribute;
import wang.liangchen.matrix.shop.product.domain.aggregate.brand.Brand;
import wang.liangchen.matrix.shop.product.domain.aggregate.category.Category;
import wang.liangchen.matrix.product.domain.aggregate.producttype.*;
import wang.liangchen.matrix.shop.product.domain.aggregate.sku.Sku;
import wang.liangchen.matrix.shop.product.domain.aggregate.spu.Spu;
import wang.liangchen.matrix.shop.product.domain.shared.DomainEvent;
import wang.liangchen.matrix.product.domain.shared.id.*;
import wang.liangchen.matrix.shop.product.domain.shared.attribute.AttributeValue;
import wang.liangchen.matrix.shop.product.domain.south.port.publisher.DomainEventPublisherPort;
import wang.liangchen.matrix.product.domain.south.port.repository.*;
import org.springframework.stereotype.Repository;
import java.util.*; import java.util.concurrent.ConcurrentHashMap;

/** Development adapter only. Replace with PostgreSQL adapters before production deployment. */
@Repository
public class InMemoryCatalogAdapter implements SpuRepositoryPort, SkuRepositoryPort, BrandRepositoryPort,
        CategoryRepositoryPort, ProductTypeRepositoryPort, AttributeRepositoryPort, DomainEventPublisherPort {
    private final Map<SpuKey, Spu> spus = new ConcurrentHashMap<>();
    private final Map<SkuKey, Sku> skus = new ConcurrentHashMap<>();
    private final Map<UUID, Brand> brands = new ConcurrentHashMap<>();
    private final Map<UUID, Category> categories = new ConcurrentHashMap<>();
    private final Map<UUID, ProductType> productTypes = new ConcurrentHashMap<>();
    private final Map<UUID, Attribute> attributes = new ConcurrentHashMap<>();
    private final List<DomainEvent> publishedEvents = Collections.synchronizedList(new ArrayList<>());

    public Spu save(Spu spu) { spus.put(new SpuKey(spu.tenantId().value(), spu.id().value()), spu); return spu; }
    public Optional<Spu> findById(TenantId tenantId, SpuId id) { return Optional.ofNullable(spus.get(new SpuKey(tenantId.value(), id.value()))); }
    public boolean existsBySpuCode(TenantId tenantId, String code) { return spus.values().stream().anyMatch(s -> s.tenantId().equals(tenantId) && s.code().value().equals(code)); }
    public Sku save(Sku sku) { skus.put(new SkuKey(sku.tenantId().value(), sku.id().value()), sku); return sku; }
    public Optional<Sku> findById(TenantId tenantId, SkuId id) { return Optional.ofNullable(skus.get(new SkuKey(tenantId.value(), id.value()))); }
    public boolean existsBySkuCode(TenantId tenantId, String code) { return skus.values().stream().anyMatch(s -> s.tenantId().equals(tenantId) && s.code().value().equals(code)); }
    public boolean existsBySpuAndVariantKey(TenantId tenantId, SpuId spuId, String key) { return skus.values().stream().anyMatch(s -> s.tenantId().equals(tenantId) && s.spuId().equals(spuId) && s.variantKey().value().equals(key)); }
    public boolean hasSkuUsingOption(TenantId tenantId, SpuId spuId, AttributeId attributeId, String optionCode) {
        return skus.values().stream().filter(s -> s.tenantId().equals(tenantId) && s.spuId().equals(spuId))
                .map(s -> s.attributes().get(attributeId)).anyMatch(v -> v instanceof AttributeValue.EnumOption e && e.optionCode().equals(optionCode));
    }
    public Optional<Brand> findById(BrandId id) { return Optional.ofNullable(brands.get(id.value())); }
    public Optional<Category> findById(CategoryId id) { return Optional.ofNullable(categories.get(id.value())); }
    public Optional<ProductType> findById(ProductTypeId id) { return Optional.ofNullable(productTypes.get(id.value())); }
    public Optional<ProductTypeVersion> findPublishedVersion(ProductTypeId id, int v) { return findById(id).map(p -> { try { return p.requireVersion(v); } catch (RuntimeException ex) { return null; } }); }
    public Optional<Attribute> findById(AttributeId id) { return Optional.ofNullable(attributes.get(id.value())); }
    public void publish(Collection<DomainEvent> events) { publishedEvents.addAll(events); }
    public List<DomainEvent> publishedEvents() { synchronized (publishedEvents) { return List.copyOf(publishedEvents); } }

    public void put(Brand brand) { brands.put(brand.id().value(), brand); }
    public void put(Category category) { categories.put(category.id().value(), category); }
    public void put(ProductType productType) { productTypes.put(productType.id().value(), productType); }
    public void put(Attribute attribute) { attributes.put(attribute.id().value(), attribute); }
    private record SpuKey(UUID tenant, UUID id) {} private record SkuKey(UUID tenant, UUID id) {}
}
