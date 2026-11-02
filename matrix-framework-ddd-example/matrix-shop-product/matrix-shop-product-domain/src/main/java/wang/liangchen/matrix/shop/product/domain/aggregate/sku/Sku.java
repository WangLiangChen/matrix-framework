package wang.liangchen.matrix.shop.product.domain.aggregate.sku;

import wang.liangchen.matrix.shop.product.domain.aggregate.producttype.ProductTypeVersion;
import wang.liangchen.matrix.product.domain.aggregate.spu.*;
import wang.liangchen.matrix.shop.product.domain.aggregate.sku.event.SkuCreatedEvent;
import wang.liangchen.matrix.shop.product.domain.shared.DomainException;
import wang.liangchen.matrix.shop.product.domain.shared.DomainEvent;
import wang.liangchen.matrix.shop.product.domain.shared.attribute.AttributeValue;
import wang.liangchen.matrix.product.domain.shared.id.*;
import java.util.*;

/** Independently persisted SKU aggregate. Inventory and commercial price are intentionally elsewhere. */
public final class Sku {
    private final SkuId id; private final TenantId tenantId; private final SpuId spuId; private final SkuCode code;
    private final VariantKey variantKey; private Map<AttributeId, AttributeValue> attributes; private List<Barcode> barcodes;
    private PackageSpecification packageSpecification; private List<MediaReference> media; private SkuStatus status; private long version;
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    private Sku(SkuId id, TenantId tenantId, SpuId spuId, SkuCode code, VariantKey key,
                Map<AttributeId, AttributeValue> attributes, List<Barcode> barcodes,
                PackageSpecification packageSpecification, List<MediaReference> media) {
        this.id = id; this.tenantId = tenantId; this.spuId = spuId; this.code = code; this.variantKey = key;
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes)); this.barcodes = List.copyOf(barcodes);
        this.packageSpecification = packageSpecification; this.media = List.copyOf(media); this.status = SkuStatus.DRAFT;
    }
    public static Sku create(Spu spu, ProductTypeVersion schema, SkuCode code, Collection<AttributeValue> attributes,
                             List<Barcode> barcodes, PackageSpecification packageSpecification, List<MediaReference> media) {
        Objects.requireNonNull(spu); Objects.requireNonNull(schema);
        if (spu.status() == SpuStatus.ARCHIVED) throw new DomainException("Cannot create SKU under archived SPU");
        List<AttributeValue> values = List.copyOf(attributes == null ? List.of() : attributes);
        spu.validateSkuAttributes(values, schema);
        Map<AttributeId, AttributeValue> map = new LinkedHashMap<>();
        for (AttributeValue value : values) if (map.put(value.attributeId(), value) != null) throw new DomainException("Duplicate SKU attribute");
        VariantKey key = VariantKey.from(spu.variantConfiguration(), values);
        Sku sku = new Sku(SkuId.newId(), spu.tenantId(), spu.id(), Objects.requireNonNull(code), key, map,
                barcodes == null ? List.of() : barcodes, packageSpecification, media == null ? List.of() : media);
        sku.domainEvents.add(new SkuCreatedEvent(spu.tenantId(), spu.id(), sku.id, key.value()));
        return sku;
    }
    public void activate() { if (status == SkuStatus.DISCONTINUED) throw new DomainException("Discontinued SKU cannot be reactivated"); status = SkuStatus.ACTIVE; version++; }
    public void deactivate() { if (status == SkuStatus.DISCONTINUED) throw new DomainException("Discontinued SKU cannot be changed"); status = SkuStatus.INACTIVE; version++; }
    public void discontinue() { status = SkuStatus.DISCONTINUED; version++; }
    public void replaceMedia(List<MediaReference> media) { this.media = List.copyOf(media); version++; }
    public void replacePackageSpecification(PackageSpecification spec) { packageSpecification = spec; version++; }
    public List<DomainEvent> pullDomainEvents() { List<DomainEvent> copy = List.copyOf(domainEvents); domainEvents.clear(); return copy; }
    public SkuId id() { return id; } public TenantId tenantId() { return tenantId; } public SpuId spuId() { return spuId; }
    public SkuCode code() { return code; } public VariantKey variantKey() { return variantKey; }
    public Map<AttributeId, AttributeValue> attributes() { return attributes; } public List<Barcode> barcodes() { return barcodes; }
    public PackageSpecification packageSpecification() { return packageSpecification; } public List<MediaReference> media() { return media; }
    public SkuStatus status() { return status; } public long version() { return version; }
}
