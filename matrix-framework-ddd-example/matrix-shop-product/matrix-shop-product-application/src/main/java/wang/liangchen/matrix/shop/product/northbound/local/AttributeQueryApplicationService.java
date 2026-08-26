package wang.liangchen.matrix.shop.product.northbound.local;

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;
import wang.liangchen.matrix.shop.product.domain.port.AttributeQueryPort;
import wang.liangchen.matrix.shop.product.domain.readmodel.AttributeSummary;
import wang.liangchen.matrix.shop.product.message.request.AttributeQueryRequest;
import wang.liangchen.matrix.shop.product.message.response.AttributeView;
import wang.liangchen.matrix.shop.product.northbound.exception.ApplicationException;

import java.util.List;
import java.util.function.Supplier;

/**
 * 属性查询应用服务：CQRS查询侧，返回属性列表视图。
 */
@Service
@ApplicationService(ApplicationServiceType.QUERY)
public class AttributeQueryApplicationService implements IQueryApplicationService {

    private final AttributeQueryPort attributeQuery;

    public AttributeQueryApplicationService(AttributeQueryPort attributeQuery) {
        this.attributeQuery = attributeQuery;
    }

    /**
     * 用例：查询属性列表。
     */
    public List<AttributeView> queryAllAttributes(AttributeQueryRequest request) {
        return useCase("查询属性列表", () -> attributeQuery.queryAllAttributes().stream().map(this::attributeView).toList());
    }

    private AttributeView attributeView(AttributeSummary summary) {
        return new AttributeView(summary.id().value(), summary.name(), summary.type().name(), summary.options());
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
