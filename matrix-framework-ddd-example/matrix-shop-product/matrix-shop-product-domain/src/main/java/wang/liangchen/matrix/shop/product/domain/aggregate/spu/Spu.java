package wang.liangchen.matrix.shop.product.domain.aggregate.spu;

import wang.liangchen.matrix.product.domain.aggregate.producttype.*;
import wang.liangchen.matrix.product.domain.aggregate.spu.event.*;
import wang.liangchen.matrix.shop.product.domain.shared.DomainException;
import wang.liangchen.matrix.shop.product.domain.shared.DomainEvent;
import wang.liangchen.matrix.product.domain.shared.attribute.*;
import wang.liangchen.matrix.product.domain.shared.id.*;
import java.util.*;

/** SPU is an independent aggregate root. It may exist and be published without any SKU. */
public final class Spu {
    private final SpuId id; private final TenantId tenantId; private final SpuCode code;
    private final ProductTypeId productTypeId; private final int productTypeVersion;
    private BrandId brandId; private String name; private String subtitle; private String description;
    private Map<AttributeId, AttributeValue> attributes; private List<SpuCategoryAssignment> categories;
    private List<MediaReference> media; private VariantConfiguration variantConfiguration;
    private SpuStatus status; private long version; private final List<DomainEvent> domainEvents = new ArrayList<>();

    private Spu(SpuId id, TenantId tenantId, SpuCode code, ProductTypeId typeId, int typeVersion, BrandId brandId,
                String name, String subtitle, String description, Map<AttributeId, AttributeValue> attrs,
                List<SpuCategoryAssignment> categories, List<MediaReference> media, VariantConfiguration variants) {
        this.id = Objects.requireNonNull(id); this.tenantId = Objects.requireNonNull(tenantId); this.code = Objects.requireNonNull(code);
        this.productTypeId = Objects.requireNonNull(typeId); this.productTypeVersion = typeVersion; this.brandId = brandId;
        this.name = require(name, "name"); this.subtitle = subtitle; this.description = description;
        this.attributes = immutableMap(attrs); this.categories = validateCategories(categories); this.media = List.copyOf(media);
        this.variantConfiguration = Objects.requireNonNull(variants); this.status = SpuStatus.DRAFT; this.version = 0;
    }
    public static Spu create(TenantId tenantId, SpuCode code, ProductTypeVersion schema, BrandId brandId,
                             String name, String subtitle, String description, Collection<AttributeValue> attributes,
                             List<SpuCategoryAssignment> categories, List<MediaReference> media, VariantConfiguration variants) {
        Objects.requireNonNull(schema); Objects.requireNonNull(tenantId);
        Map<AttributeId, AttributeValue> values = toMap(attributes);
        validateAttributes(values.values(), schema, ProductTypeAttribute.Scope.SPU, false);
        variants.validateAgainst(schema);
        Spu spu = new Spu(SpuId.newId(), tenantId, SpuCodeCheck(code), schema.productTypeId(), schema.version(), brandId,
                name, subtitle, description, values, categories, media == null ? List.of() : media, variants);
        spu.domainEvents.add(new SpuCreatedEvent(tenantId, spu.id));
        return spu;
    }
    public void reviseProductInformation(String name, String subtitle, String description, BrandId brandId,
                                         Collection<AttributeValue> attrs, ProductTypeVersion schema) {
        ensureMutable();
        if (!schema.productTypeId().equals(productTypeId) || schema.version() != productTypeVersion)
            throw new DomainException("SPU is pinned to a different ProductType version; migrate explicitly");
        Map<AttributeId, AttributeValue> values = toMap(attrs);
        validateAttributes(values.values(), schema, ProductTypeAttribute.Scope.SPU, false);
        this.name = require(name, "name"); this.subtitle = subtitle; this.description = description;
        this.brandId = brandId; this.attributes = values; version++;
    }
    public void configureVariants(VariantConfiguration config, ProductTypeVersion schema) {
        ensureMutable();
        if (!schema.productTypeId().equals(productTypeId) || schema.version() != productTypeVersion)
            throw new DomainException("ProductType version mismatch");
        config.validateAgainst(schema); this.variantConfiguration = config; version++;
    }
    public void assignCategories(List<SpuCategoryAssignment> assignments) { ensureMutable(); categories = validateCategories(assignments); version++; }
    public void replaceMedia(List<MediaReference> media) { ensureMutable(); this.media = List.copyOf(media); version++; }
    public void submitForReview(ProductTypeVersion schema) {
        ensureMutable(); validateForPublication(schema); status = SpuStatus.PENDING_REVIEW; version++;
    }
    public void publish(ProductTypeVersion schema) {
        ensureMutable(); validateForPublication(schema); status = SpuStatus.PUBLISHED; version++;
        domainEvents.add(new SpuPublishedEvent(tenantId, id, productTypeVersion));
    }
    public void disable() { if (status == SpuStatus.ARCHIVED) throw new DomainException("Archived SPU cannot be disabled"); status = SpuStatus.DISABLED; version++; }
    public void archive() { status = SpuStatus.ARCHIVED; version++; }
    private void validateForPublication(ProductTypeVersion schema) {
        if (!schema.productTypeId().equals(productTypeId) || schema.version() != productTypeVersion) throw new DomainException("ProductType version mismatch");
        validateAttributes(attributes.values(), schema, ProductTypeAttribute.Scope.SPU, true);
        variantConfiguration.validateAgainst(schema);
        // Deliberately no SKU-existence check: SPU lifecycle is independent of SKU lifecycle.
    }
    public void validateSkuAttributes(Collection<AttributeValue> values, ProductTypeVersion schema) {
        if (!schema.productTypeId().equals(productTypeId) || schema.version() != productTypeVersion) throw new DomainException("ProductType version mismatch");
        validateAttributes(values, schema, ProductTypeAttribute.Scope.SKU, true);
        Map<AttributeId, AttributeValue> byId = toMap(values);
        for (VariantDimension dim : variantConfiguration.dimensions()) {
            AttributeValue value = byId.get(dim.attributeId());
            if (!(value instanceof AttributeValue.EnumOption selected))
                throw new DomainException("SKU must have exactly one value for dimension " + dim.attributeCode());
            if (!dim.optionCodes().contains(selected.optionCode()))
                throw new DomainException("SKU value is not enabled by SPU for " + dim.attributeCode());
        }
    }
    private static void validateAttributes(Collection<AttributeValue> values, ProductTypeVersion schema,
                                           ProductTypeAttribute.Scope scope, boolean checkRequired) {
        Map<AttributeId, ProductTypeAttribute> defs = new HashMap<>();
        schema.attributesFor(scope).forEach(d -> defs.put(d.attributeId(), d));
        for (AttributeValue value : values) {
            ProductTypeAttribute def = defs.get(value.attributeId());
            if (def == null) throw new DomainException("Attribute is not allowed in " + scope + ": " + value.attributeId());
            try { AttributeValueValidator.validate(value, def); } catch (IllegalArgumentException ex) { throw new DomainException(ex.getMessage()); }
        }
        if (checkRequired) {
            try { AttributeValueValidator.validateRequired(schema.attributes(), values, scope); }
            catch (IllegalArgumentException ex) { throw new DomainException(ex.getMessage()); }
        }
    }
    private static Map<AttributeId, AttributeValue> toMap(Collection<AttributeValue> values) {
        Map<AttributeId, AttributeValue> map = new LinkedHashMap<>();
        if (values != null) for (AttributeValue value : values) if (map.put(value.attributeId(), value) != null)
            throw new DomainException("Duplicate attribute value: " + value.attributeId());
        return Collections.unmodifiableMap(map);
    }
    private static Map<AttributeId, AttributeValue> immutableMap(Map<AttributeId, AttributeValue> map) { return Collections.unmodifiableMap(new LinkedHashMap<>(map)); }
    private static List<SpuCategoryAssignment> validateCategories(List<SpuCategoryAssignment> values) {
        List<SpuCategoryAssignment> result = List.copyOf(values == null ? List.of() : values);
        Set<CategoryId> ids = new HashSet<>(); int primary = 0;
        for (var item : result) { if (!ids.add(item.categoryId())) throw new DomainException("Duplicate category assignment"); if (item.primary()) primary++; }
        if (primary > 1) throw new DomainException("Only one primary category is allowed"); return result;
    }
    private void ensureMutable() { if (status == SpuStatus.ARCHIVED) throw new DomainException("Archived SPU is immutable"); }
    public List<DomainEvent> pullDomainEvents() { List<DomainEvent> copy = List.copyOf(domainEvents); domainEvents.clear(); return copy; }
    public SpuId id() { return id; } public TenantId tenantId() { return tenantId; } public SpuCode code() { return code; }
    public ProductTypeId productTypeId() { return productTypeId; } public int productTypeVersion() { return productTypeVersion; }
    public BrandId brandId() { return brandId; } public String name() { return name; } public String subtitle() { return subtitle; }
    public String description() { return description; } public Map<AttributeId, AttributeValue> attributes() { return attributes; }
    public List<SpuCategoryAssignment> categories() { return categories; } public List<MediaReference> media() { return media; }
    public VariantConfiguration variantConfiguration() { return variantConfiguration; } public SpuStatus status() { return status; }
    public long version() { return version; }
    private static SpuCode SpuCodeCheck(SpuCode code) { return Objects.requireNonNull(code); }
    private static String require(String value, String field) { if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " required"); return value; }
}
