package wang.liangchen.matrix.shop.product.domain.aggregate.producttype;

import java.util.Objects;
import java.util.UUID;

public record ProductTypeId(UUID value) {
    public ProductTypeId { Objects.requireNonNull(value, "value"); }
    public static ProductTypeId newId() { return new ProductTypeId(UUID.randomUUID()); }
    public static ProductTypeId of(String value) { return new ProductTypeId(UUID.fromString(value)); }
    @Override public String toString() { return value.toString(); }
}
