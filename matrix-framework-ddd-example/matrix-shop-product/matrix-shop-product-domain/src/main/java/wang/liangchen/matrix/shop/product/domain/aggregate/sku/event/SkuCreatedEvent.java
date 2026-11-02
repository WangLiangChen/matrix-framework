package wang.liangchen.matrix.shop.product.domain.aggregate.sku.event;
import wang.liangchen.matrix.shop.product.domain.shared.DomainEvent;
import wang.liangchen.matrix.product.domain.shared.id.*;
import java.time.Instant; import java.util.UUID;
public record SkuCreatedEvent(UUID eventId, Instant occurredAt, TenantId tenantId, SpuId spuId, SkuId skuId, String variantKey) implements DomainEvent {
    public SkuCreatedEvent(TenantId tenantId, SpuId spuId, SkuId skuId, String key) { this(UUID.randomUUID(), Instant.now(), tenantId, spuId, skuId, key); }
    public String eventType() { return "product.sku.created.v1"; }
}
