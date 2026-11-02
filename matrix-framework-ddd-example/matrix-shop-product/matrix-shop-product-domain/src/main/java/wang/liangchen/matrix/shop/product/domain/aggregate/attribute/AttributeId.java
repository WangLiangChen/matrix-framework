package wang.liangchen.matrix.shop.product.domain.aggregate.attribute;

import java.util.Objects;
import java.util.UUID;

public record AttributeId(UUID value) {
    public AttributeId {
        Objects.requireNonNull(value, "value");
    }

    public static AttributeId newId() {
        return new AttributeId(UUID.randomUUID());
    }

    public static AttributeId of(String value) {
        return new AttributeId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
