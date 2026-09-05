package wang.liangchen.matrix.shop.product.northbound.local;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.ICommandApplicationService;
import wang.liangchen.matrix.shop.product.domain.attribute.Attribute;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeFactory;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeId;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeType;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;
import wang.liangchen.matrix.shop.product.southbound.port.AttributeRepositoryPort;
import wang.liangchen.matrix.shop.product.southbound.port.DomainEventPublisherPort;
import wang.liangchen.matrix.shop.product.message.request.AddAttributeOptionCommandRequest;
import wang.liangchen.matrix.shop.product.message.request.CreateAttributeCommandRequest;
import wang.liangchen.matrix.shop.product.message.response.AddAttributeOptionResult;
import wang.liangchen.matrix.shop.product.message.response.CreateAttributeResult;
import wang.liangchen.matrix.shop.product.northbound.exception.ApplicationException;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 属性命令应用服务：编排属性聚合实现命令用例。
 */
@Service
@ApplicationService(ApplicationServiceType.COMMAND)
public class AttributeCommandApplicationService implements ICommandApplicationService {

    private final AttributeRepositoryPort attributeRepository;
    private final DomainEventPublisherPort eventPublisher;
    private final AttributeFactory attributeFactory = new AttributeFactory();

    public AttributeCommandApplicationService(AttributeRepositoryPort attributeRepository, DomainEventPublisherPort eventPublisher) {
        this.attributeRepository = attributeRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 用例：创建属性。
     */
    @Transactional
    public CreateAttributeResult createAttribute(CreateAttributeCommandRequest request) {
        return useCase("创建属性", () -> {
            Attribute attribute = attributeFactory.create(request.name(), attributeType(request.type()),
                    request.options() == null ? List.of() : request.options());
            attributeRepository.save(attribute);
            eventPublisher.publish(attribute.events());
            attribute.clearEvents();
            return new CreateAttributeResult(attribute.id().value());
        });
    }

    /**
     * 用例：增加属性选项。
     */
    @Transactional
    public AddAttributeOptionResult addAttributeOption(AddAttributeOptionCommandRequest request) {
        return useCase("增加属性选项", () -> {
            Attribute attribute = mutate(AttributeId.of(request.attributeId()), a -> a.addOption(request.option()));
            return new AddAttributeOptionResult(attribute.id().value(), request.option());
        });
    }

    private Attribute mutate(AttributeId attributeId, Consumer<Attribute> mutation) {
        Attribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new DomainException("属性不存在：" + attributeId.value()));
        mutation.accept(attribute);
        attributeRepository.save(attribute);
        eventPublisher.publish(attribute.events());
        attribute.clearEvents();
        return attribute;
    }

    private AttributeType attributeType(String type) {
        try {
            return AttributeType.valueOf(type);
        } catch (IllegalArgumentException ex) {
            throw new DomainException("属性类型无效：" + type);
        }
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
