package wang.liangchen.matrix.shop.product.domain.shared;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {
    UUID eventId();
    Instant occurredAt();
    String eventType();
}
