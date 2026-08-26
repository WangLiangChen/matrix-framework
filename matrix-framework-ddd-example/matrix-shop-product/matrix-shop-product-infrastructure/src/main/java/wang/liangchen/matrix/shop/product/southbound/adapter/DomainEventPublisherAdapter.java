package wang.liangchen.matrix.shop.product.southbound.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.ddd.domain.event.IDomainEvent;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IPublisherAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.port.DomainEventPublisherPort;

import java.util.List;

/**
 * 领域事件发布适配器：实现领域事件发布端口，隔离对事件总线的访问。
 * POC简化：在应用服务保存聚合后同步发布；生产环境建议通过after-commit机制
 * 在事务提交后发布，避免事件与业务变更不一致。
 */
@Component("productDomainEventPublisherAdapter")
@Adapter(PortType.Publisher)
public class DomainEventPublisherAdapter implements DomainEventPublisherPort, IPublisherAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventPublisherAdapter.class);

    private final ApplicationEventPublisher eventPublisher;

    public DomainEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(List<IDomainEvent> domainEvents) {
        domainEvents.forEach(event -> {
            LOGGER.info("发布领域事件：{}", event.getClass().getSimpleName());
            eventPublisher.publishEvent(event);
        });
    }
}
