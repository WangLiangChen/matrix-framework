package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.category.Category;
import wang.liangchen.matrix.shop.product.domain.category.CategoryFactory;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.port.CategoryQueryPort;
import wang.liangchen.matrix.shop.product.domain.port.CategoryRepositoryPort;
import wang.liangchen.matrix.shop.product.domain.readmodel.CategorySummary;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 类目仓储适配器：实现类目仓储端口与查询端口，完成类目聚合与持久化对象之间的防腐翻译，
 * 查询侧将平铺的类目持久化对象装配为类目树读模型。
 */
@Repository
@Adapter(PortType.Repository)
public class CategoryRepositoryAdapter implements CategoryRepositoryPort, CategoryQueryPort, IRepositoryAdapter {

    private final CategoryDao categoryDao;
    private final CategoryFactory categoryFactory = new CategoryFactory();

    public CategoryRepositoryAdapter(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(CategoryId categoryId) {
        return categoryDao.findById(categoryId.value()).map(this::reconstitute);
    }

    @Override
    public void save(Category category) {
        categoryDao.save(toPo(category));
    }

    @Override
    public void remove(Category category) {
        categoryDao.deleteById(category.id().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummary> queryCategoryTree() {
        Map<String, List<CategoryPo>> byParent = categoryDao.findAllByOrderByNameAsc().stream()
                .collect(Collectors.groupingBy(po -> po.getParentId() == null ? "" : po.getParentId()));
        return byParent.getOrDefault("", List.of()).stream()
                .map(po -> build(po, byParent))
                .toList();
    }

    private CategorySummary build(CategoryPo po, Map<String, List<CategoryPo>> byParent) {
        List<CategorySummary> children = byParent.getOrDefault(po.getId(), List.of()).stream()
                .map(child -> build(child, byParent))
                .toList();
        return new CategorySummary(CategoryId.of(po.getId()), po.getName(),
                po.getParentId() == null ? null : CategoryId.of(po.getParentId()), children);
    }

    private Category reconstitute(CategoryPo po) {
        return categoryFactory.reconstitute(CategoryId.of(po.getId()), po.getName(),
                po.getParentId() == null ? null : CategoryId.of(po.getParentId()));
    }

    private CategoryPo toPo(Category category) {
        CategoryPo po = new CategoryPo();
        po.setId(category.id().value());
        po.setName(category.name());
        po.setParentId(category.parentId() == null ? null : category.parentId().value());
        return po;
    }
}
