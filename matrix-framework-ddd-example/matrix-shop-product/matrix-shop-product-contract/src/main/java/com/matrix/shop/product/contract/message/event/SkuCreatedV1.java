package wang.liangchen.matrix.product.contract.message.event;
import java.time.Instant; import java.util.UUID;
public record SkuCreatedV1(UUID eventId, Instant occurredAt, UUID tenantId, UUID spuId, UUID skuId, String variantKey) {}
