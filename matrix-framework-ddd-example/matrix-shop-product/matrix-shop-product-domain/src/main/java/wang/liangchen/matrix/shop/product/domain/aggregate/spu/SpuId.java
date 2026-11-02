package wang.liangchen.matrix.shop.product.domain.aggregate.spu;

import java.util.Objects;
import java.util.UUID;

public record SpuId(UUID value) {
    public SpuId { Objects.requireNonNull(value, "value"); }
    public static SpuId newId() { return new SpuId(UUID.randomUUID()); }
    public static SpuId of(String value) { return new SpuId(UUID.fromString(value)); }
    @Override public String toString() { return value.toString(); }
}
