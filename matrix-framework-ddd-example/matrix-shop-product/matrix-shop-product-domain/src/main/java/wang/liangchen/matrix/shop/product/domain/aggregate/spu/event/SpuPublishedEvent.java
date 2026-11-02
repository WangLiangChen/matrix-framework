package wang.liangchen.matrix.shop.product.domain.aggregate.spu.event;
import wang.liangchen.matrix.shop.product.domain.shared.DomainEvent;
import wang.liangchen.matrix.product.domain.shared.id.*;
import java.time.Instant; import java.util.UUID;
public record SpuPublishedEvent(UUID eventId, Instant occurredAt, TenantId tenantId, SpuId spuId, int productTypeVersion) implements DomainEvent {
    public SpuPublishedEvent(TenantId tenantId, SpuId spuId, int productTypeVersion) { this(UUID.randomUUID(), Instant.now(), tenantId, spuId, productTypeVersion); }
    public String eventType() { return "product.spu.published.v1"; }
}
