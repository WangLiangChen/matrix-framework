package wang.liangchen.matrix.shop.product.domain.south.port.publisher;
import wang.liangchen.matrix.shop.product.domain.shared.DomainEvent;
import java.util.Collection;
public interface DomainEventPublisherPort { void publish(Collection<DomainEvent> events); }
