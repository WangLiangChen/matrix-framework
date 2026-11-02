package wang.liangchen.matrix.shop.product.domain.aggregate.spu.event;
import wang.liangchen.matrix.shop.product.domain.shared.DomainEvent;
import wang.liangchen.matrix.product.domain.shared.id.*;
import java.time.Instant; import java.util.UUID;
public record SpuCreatedEvent(UUID eventId, Instant occurredAt, TenantId tenantId, SpuId spuId) implements DomainEvent {
    public SpuCreatedEvent(TenantId tenantId, SpuId spuId) { this(UUID.randomUUID(), Instant.now(), tenantId, spuId); }
    public String eventType() { return "product.spu.created.v1"; }
}
