package wang.liangchen.matrix.shop.product.northbound.local;

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;
import wang.liangchen.matrix.shop.product.domain.port.CategoryQueryPort;
import wang.liangchen.matrix.shop.product.domain.readmodel.CategorySummary;
import wang.liangchen.matrix.shop.product.message.request.CategoryQueryRequest;
import wang.liangchen.matrix.shop.product.message.response.CategoryView;
import wang.liangchen.matrix.shop.product.northbound.exception.ApplicationException;

import java.util.List;
import java.util.function.Supplier;

/**
 * 类目查询应用服务：CQRS查询侧，返回类目树视图。
 */
@Service
@ApplicationService(ApplicationServiceType.QUERY)
public class CategoryQueryApplicationService implements IQueryApplicationService {

    private final CategoryQueryPort categoryQuery;

    public CategoryQueryApplicationService(CategoryQueryPort categoryQuery) {
        this.categoryQuery = categoryQuery;
    }

    /**
     * 用例：查询类目树。
     */
    public List<CategoryView> queryCategoryTree(CategoryQueryRequest request) {
        return useCase("查询类目树", () -> categoryQuery.queryCategoryTree().stream().map(this::categoryView).toList());
    }

    private CategoryView categoryView(CategorySummary summary) {
        return new CategoryView(summary.id().value(), summary.name(),
                summary.parentId() == null ? null : summary.parentId().value(),
                summary.children().stream().map(this::categoryView).toList());
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
