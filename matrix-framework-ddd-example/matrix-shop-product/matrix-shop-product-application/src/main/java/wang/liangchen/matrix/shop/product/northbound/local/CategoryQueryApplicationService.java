package wang.liangchen.matrix.shop.product.northbound.local;

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;
import wang.liangchen.matrix.shop.product.domain.category.Category;
import wang.liangchen.matrix.shop.product.domain.port.CategoryRepositoryPort;
import wang.liangchen.matrix.shop.product.message.request.CategoryQueryRequest;
import wang.liangchen.matrix.shop.product.message.response.CategoryView;
import wang.liangchen.matrix.shop.product.northbound.exception.ApplicationException;

import java.util.List;
import java.util.function.Supplier;

/**
 * 类目查询应用服务：CQRS查询侧，经类目仓储端口只读获取类目聚合，
 * 在应用层将平铺的类目聚合装配为类目树视图。
 */
@Service
@ApplicationService(ApplicationServiceType.QUERY)
public class CategoryQueryApplicationService implements IQueryApplicationService {

    private final CategoryRepositoryPort categoryRepository;

    public CategoryQueryApplicationService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 用例：查询类目树。
     */
    public List<CategoryView> queryCategoryTree(CategoryQueryRequest request) {
        return useCase("查询类目树", () -> {
            List<Category> categories = categoryRepository.findAll();
            return categories.stream()
                    .filter(category -> category.parentId() == null)
                    .map(category -> categoryView(category, categories))
                    .toList();
        });
    }

    private CategoryView categoryView(Category category, List<Category> all) {
        List<CategoryView> children = all.stream()
                .filter(child -> category.id().equals(child.parentId()))
                .map(child -> categoryView(child, all))
                .toList();
        return new CategoryView(category.id().value(), category.name(),
                category.parentId() == null ? null : category.parentId().value(), children);
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
