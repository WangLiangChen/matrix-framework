package wang.liangchen.matrix.shop.product.domain.aggregate.category;

import wang.liangchen.matrix.shop.product.domain.aggregate.producttype.ProductTypeId;
import java.util.Set;

/** Empty allow-list means unrestricted only when restricted=false. */
public record CategoryProductTypePolicy(boolean restricted, Set<ProductTypeId> allowedProductTypeIds) {
    public CategoryProductTypePolicy { allowedProductTypeIds = Set.copyOf(allowedProductTypeIds == null ? Set.of() : allowedProductTypeIds); }
    public static CategoryProductTypePolicy unrestricted() { return new CategoryProductTypePolicy(false, Set.of()); }
    public boolean allows(ProductTypeId id) { return !restricted || allowedProductTypeIds.contains(id); }
}
