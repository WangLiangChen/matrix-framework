package wang.liangchen.matrix.shop.product.domain.aggregate.sku;

import java.util.Objects;
import java.util.UUID;

public record SkuId(UUID value) {
    public SkuId { Objects.requireNonNull(value, "value"); }
    public static SkuId newId() { return new SkuId(UUID.randomUUID()); }
    public static SkuId of(String value) { return new SkuId(UUID.fromString(value)); }
    @Override public String toString() { return value.toString(); }
}
