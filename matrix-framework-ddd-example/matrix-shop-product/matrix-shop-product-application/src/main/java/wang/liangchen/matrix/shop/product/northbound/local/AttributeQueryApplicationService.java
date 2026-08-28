package wang.liangchen.matrix.shop.product.northbound.local;

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;
import wang.liangchen.matrix.shop.product.domain.port.AttributeRepositoryPort;
import wang.liangchen.matrix.shop.product.message.request.AttributeQueryRequest;
import wang.liangchen.matrix.shop.product.message.response.AttributeView;
import wang.liangchen.matrix.shop.product.northbound.exception.ApplicationException;

import java.util.List;
import java.util.function.Supplier;

/**
 * 属性查询应用服务：CQRS查询侧，经属性仓储端口只读获取属性聚合并装配为视图。
 */
@Service
@ApplicationService(ApplicationServiceType.QUERY)
public class AttributeQueryApplicationService implements IQueryApplicationService {

    private final AttributeRepositoryPort attributeRepository;

    public AttributeQueryApplicationService(AttributeRepositoryPort attributeRepository) {
        this.attributeRepository = attributeRepository;
    }

    /**
     * 用例：查询属性列表。
     */
    public List<AttributeView> queryAllAttributes(AttributeQueryRequest request) {
        return useCase("查询属性列表", () -> attributeRepository.findAll().stream()
                .map(attribute -> new AttributeView(attribute.id().value(), attribute.name(),
                        attribute.type().name(), attribute.options()))
                .toList());
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
