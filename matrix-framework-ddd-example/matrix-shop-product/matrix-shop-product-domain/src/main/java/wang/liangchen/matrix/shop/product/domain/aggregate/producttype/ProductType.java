package wang.liangchen.matrix.shop.product.domain.aggregate.producttype;

import wang.liangchen.matrix.shop.product.domain.shared.DomainException;

import java.time.Instant;
import java.util.*;

/** Platform-owned type template. Published versions are immutable snapshots. */
public final class ProductType {
    public enum Status { DRAFT, PUBLISHED, DISABLED }
    private final ProductTypeId id;
    private final String code;
    private String name;
    private String description;
    private Status status;
    private List<ProductTypeAttribute> draftAttributes;
    private final NavigableMap<Integer, ProductTypeVersion> versions;
    private long version;

    private ProductType(ProductTypeId id, String code, String name, String description, List<ProductTypeAttribute> attributes) {
        this.id = Objects.requireNonNull(id); this.code = require(code, "code"); this.name = require(name, "name");
        this.description = description; this.status = Status.DRAFT; this.draftAttributes = List.copyOf(attributes);
        this.versions = new TreeMap<>(); this.version = 0;
    }
    public static ProductType create(String code, String name, String description, List<ProductTypeAttribute> attributes) {
        return create(ProductTypeId.newId(), code, name, description, attributes);
    }
    public static ProductType create(ProductTypeId id, String code, String name, String description, List<ProductTypeAttribute> attributes) {
        return new ProductType(id, code, name, description, attributes);
    }
    public void reviseDraft(String name, String description, List<ProductTypeAttribute> attributes) {
        if (status != Status.DRAFT) throw new DomainException("Start a new draft before revising a published ProductType");
        this.name = require(name, "name"); this.description = description; this.draftAttributes = List.copyOf(attributes); validateAttributes(); version++;
    }
    public ProductTypeVersion publishDraft() {
        if (status == Status.DISABLED) throw new DomainException("Disabled ProductType cannot be published");
        validateAttributes();
        ProductTypeVersion published = new ProductTypeVersion(id, versions.isEmpty() ? 1 : versions.lastKey() + 1, Instant.now(), draftAttributes);
        versions.put(published.version(), published); status = Status.PUBLISHED; version++;
        return published;
    }
    public void startNextDraft() {
        if (versions.isEmpty()) throw new DomainException("Publish the initial version before starting a revision");
        if (status == Status.DISABLED) throw new DomainException("Disabled ProductType cannot be revised");
        draftAttributes = List.copyOf(versions.lastEntry().getValue().attributes()); status = Status.DRAFT; version++;
    }
    public ProductTypeVersion requireVersion(int number) {
        ProductTypeVersion result = versions.get(number);
        if (result == null) throw new DomainException("ProductType version not found: " + number);
        return result;
    }
    public void disable() { status = Status.DISABLED; version++; }
    public void enable() { if (versions.isEmpty()) status = Status.DRAFT; else status = Status.PUBLISHED; version++; }
    private void validateAttributes() {
        Set<Object> ids = new HashSet<>(); Set<String> codes = new HashSet<>();
        for (ProductTypeAttribute a : draftAttributes) {
            if (!ids.add(a.attributeId())) throw new DomainException("Duplicate attribute in ProductType: " + a.attributeCode());
            if (!codes.add(a.attributeCode())) throw new DomainException("Duplicate attribute code in ProductType: " + a.attributeCode());
        }
    }
    public ProductTypeId id() { return id; }
    public String code() { return code; }
    public String name() { return name; }
    public String description() { return description; }
    public Status status() { return status; }
    public List<ProductTypeAttribute> draftAttributes() { return draftAttributes; }
    public List<ProductTypeVersion> publishedVersions() { return List.copyOf(versions.values()); }
    public long version() { return version; }
    private static String require(String v, String n) { if (v == null || v.isBlank()) throw new IllegalArgumentException(n + " required"); return v; }
}
