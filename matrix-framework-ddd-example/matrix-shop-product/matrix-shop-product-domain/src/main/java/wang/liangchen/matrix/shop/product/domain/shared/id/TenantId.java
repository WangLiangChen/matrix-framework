package wang.liangchen.matrix.shop.product.domain.shared.id;

import java.util.Objects;
import java.util.UUID;

public record TenantId(UUID value) {
    public TenantId { Objects.requireNonNull(value, "value"); }
    public static TenantId newId() { return new TenantId(UUID.randomUUID()); }
    public static TenantId of(String value) { return new TenantId(UUID.fromString(value)); }
    @Override public String toString() { return value.toString(); }
}
