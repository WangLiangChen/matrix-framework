package wang.liangchen.matrix.shop.product.northbound.local;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.ICommandApplicationService;
import wang.liangchen.matrix.shop.product.domain.category.Category;
import wang.liangchen.matrix.shop.product.domain.category.CategoryFactory;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;
import wang.liangchen.matrix.shop.product.domain.port.CategoryRepositoryPort;
import wang.liangchen.matrix.shop.product.domain.port.DomainEventPublisherPort;
import wang.liangchen.matrix.shop.product.message.request.CreateCategoryCommandRequest;
import wang.liangchen.matrix.shop.product.message.request.MoveCategoryCommandRequest;
import wang.liangchen.matrix.shop.product.message.request.RenameCategoryCommandRequest;
import wang.liangchen.matrix.shop.product.message.response.CreateCategoryResult;
import wang.liangchen.matrix.shop.product.message.response.MoveCategoryResult;
import wang.liangchen.matrix.shop.product.message.response.RenameCategoryResult;
import wang.liangchen.matrix.shop.product.northbound.exception.ApplicationException;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 类目命令应用服务：编排类目聚合实现命令用例。
 */
@Service
@ApplicationService(ApplicationServiceType.COMMAND)
public class CategoryCommandApplicationService implements ICommandApplicationService {

    private final CategoryRepositoryPort categoryRepository;
    private final DomainEventPublisherPort eventPublisher;
    private final CategoryFactory categoryFactory = new CategoryFactory();

    public CategoryCommandApplicationService(CategoryRepositoryPort categoryRepository, DomainEventPublisherPort eventPublisher) {
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 用例：创建类目。
     */
    @Transactional
    public CreateCategoryResult createCategory(CreateCategoryCommandRequest request) {
        return useCase("创建类目", () -> {
            CategoryId parentId = request.parentId() == null ? null : CategoryId.of(request.parentId());
            Category category = categoryFactory.create(request.name(), parentId);
            categoryRepository.save(category);
            eventPublisher.publish(category.events());
            category.clearEvents();
            return new CreateCategoryResult(category.id().value());
        });
    }

    /**
     * 用例：重命名类目。
     */
    @Transactional
    public RenameCategoryResult renameCategory(RenameCategoryCommandRequest request) {
        return useCase("重命名类目", () -> {
            Category category = mutate(CategoryId.of(request.categoryId()), c -> c.rename(request.name()));
            return new RenameCategoryResult(category.id().value(), category.name());
        });
    }

    /**
     * 用例：移动类目。
     */
    @Transactional
    public MoveCategoryResult moveCategory(MoveCategoryCommandRequest request) {
        return useCase("移动类目", () -> {
            CategoryId newParentId = request.newParentId() == null ? null : CategoryId.of(request.newParentId());
            Category category = mutate(CategoryId.of(request.categoryId()), c -> c.moveTo(newParentId));
            return new MoveCategoryResult(category.id().value(), request.newParentId());
        });
    }

    private Category mutate(CategoryId categoryId, Consumer<Category> mutation) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainException("类目不存在：" + categoryId.value()));
        mutation.accept(category);
        categoryRepository.save(category);
        eventPublisher.publish(category.events());
        category.clearEvents();
        return category;
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
