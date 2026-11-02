package wang.liangchen.matrix.shop.product.domain.aggregate.category;

import java.util.Objects;
import java.util.UUID;

public record CategoryId(UUID value) {
    public CategoryId { Objects.requireNonNull(value, "value"); }
    public static CategoryId newId() { return new CategoryId(UUID.randomUUID()); }
    public static CategoryId of(String value) { return new CategoryId(UUID.fromString(value)); }
    @Override public String toString() { return value.toString(); }
}
