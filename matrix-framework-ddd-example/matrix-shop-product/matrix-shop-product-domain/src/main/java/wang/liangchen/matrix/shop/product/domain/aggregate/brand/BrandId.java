package wang.liangchen.matrix.shop.product.domain.aggregate.brand;

import java.util.Objects;
import java.util.UUID;

public record BrandId(UUID value) {
    public BrandId {
        Objects.requireNonNull(value, "value");
    }

    public static BrandId newId() {
        return new BrandId(UUID.randomUUID());
    }

    public static BrandId of(String value) {
        return new BrandId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
