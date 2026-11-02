package wang.liangchen.matrix.shop.product.domain.aggregate.spu;

import wang.liangchen.matrix.shop.product.domain.aggregate.category.CategoryId;
import java.time.Instant;
import java.util.Objects;

public record SpuCategoryAssignment(CategoryId categoryId, boolean primary, Instant assignedAt) {
    public SpuCategoryAssignment { Objects.requireNonNull(categoryId); assignedAt = assignedAt == null ? Instant.now() : assignedAt; }
    public static SpuCategoryAssignment primary(CategoryId id) { return new SpuCategoryAssignment(id, true, Instant.now()); }
    public static SpuCategoryAssignment secondary(CategoryId id) { return new SpuCategoryAssignment(id, false, Instant.now()); }
}
