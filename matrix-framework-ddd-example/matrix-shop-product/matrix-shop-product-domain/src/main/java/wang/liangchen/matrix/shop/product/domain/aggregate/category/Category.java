package wang.liangchen.matrix.shop.product.domain.aggregate.category;

import wang.liangchen.matrix.shop.product.domain.shared.DomainException;
import wang.liangchen.matrix.shop.product.domain.aggregate.producttype.ProductTypeId;
import java.util.Objects;

/** Platform-owned navigation taxonomy node; it does not define product attributes. */
public final class Category {
    public enum Status { ENABLED, DISABLED }
    private final CategoryId id; private final String code; private CategoryId parentCategoryId;
    private String name; private String slug; private String description; private String imageMediaId;
    private int sortOrder; private CategoryProductTypePolicy productTypePolicy; private Status status; private long version;
    private Category(CategoryId id, String code, CategoryId parent, String name, String slug, String description,
                     String imageMediaId, int sortOrder, CategoryProductTypePolicy policy) {
        this.id = Objects.requireNonNull(id); this.code = require(code); this.parentCategoryId = parent; this.name = require(name);
        this.slug = require(slug); this.description = description; this.imageMediaId = imageMediaId; this.sortOrder = sortOrder;
        this.productTypePolicy = Objects.requireNonNull(policy); this.status = Status.ENABLED;
    }
    public static Category create(String code, CategoryId parent, String name, String slug, String description,
                                   String imageMediaId, int sortOrder, CategoryProductTypePolicy policy) {
        return create(CategoryId.newId(), code, parent, name, slug, description, imageMediaId, sortOrder, policy);
    }
    public static Category create(CategoryId id, String code, CategoryId parent, String name, String slug, String description,
                                   String imageMediaId, int sortOrder, CategoryProductTypePolicy policy) {
        return new Category(id, code, parent, name, slug, description, imageMediaId, sortOrder, policy);
    }
    public void rename(String name, String slug) { this.name = require(name); this.slug = require(slug); version++; }
    public void changeParent(CategoryId parent) { if (id.equals(parent)) throw new DomainException("Category cannot be its own parent"); parentCategoryId = parent; version++; }
    public void changeProductTypePolicy(CategoryProductTypePolicy policy) { productTypePolicy = Objects.requireNonNull(policy); version++; }
    public void assertAllows(ProductTypeId productTypeId) { if (!productTypePolicy.allows(productTypeId)) throw new DomainException("ProductType is not allowed in category " + code); }
    public void disable() { status = Status.DISABLED; version++; } public void enable() { status = Status.ENABLED; version++; }
    public void assertUsable() { if (status != Status.ENABLED) throw new DomainException("Category is disabled: " + code); }
    public CategoryId id() { return id; } public String code() { return code; } public CategoryId parentCategoryId() { return parentCategoryId; }
    public String name() { return name; } public String slug() { return slug; } public String description() { return description; }
    public String imageMediaId() { return imageMediaId; } public int sortOrder() { return sortOrder; }
    public CategoryProductTypePolicy productTypePolicy() { return productTypePolicy; } public Status status() { return status; } public long version() { return version; }
    private static String require(String v) { if (v == null || v.isBlank()) throw new IllegalArgumentException("value required"); return v; }
}
