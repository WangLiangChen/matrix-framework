package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.AbstractRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.category.Category;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.southbound.port.CategoryRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * 类目仓储适配器：实现类目仓储端口，完成类目聚合与持久化对象之间的防腐翻译，
 * 重建聚合时委托类目聚合自身的reconstitute静态方法；查询读侧返回聚合根，类目树由应用层装配。
 */
@Repository
@Adapter(PortType.Repository)
public class CategoryRepositoryAdapter extends AbstractRepositoryAdapter<CategoryId, Category, CategoryPo> implements CategoryRepositoryPort {

    private final CategoryDao categoryDao;

    public CategoryRepositoryAdapter(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    protected Optional<CategoryPo> doFindById(CategoryId id) {
        return categoryDao.findById(id.value());
    }

    @Override
    protected void doSave(CategoryPo po) {
        categoryDao.save(po);
    }

    @Override
    protected void doRemoveById(CategoryId id) {
        categoryDao.deleteById(id.value());
    }

    @Override
    protected Category reconstitute(CategoryPo po) {
        return Category.reconstitute(CategoryId.of(po.getId()), po.getName(),
                po.getParentId() == null ? null : CategoryId.of(po.getParentId()));
    }

    @Override
    protected CategoryPo toPo(Category category) {
        CategoryPo po = new CategoryPo();
        po.setId(category.id().value());
        po.setName(category.name());
        po.setParentId(category.parentId() == null ? null : category.parentId().value());
        return po;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryDao.findAllByOrderByNameAsc().stream()
                .map(this::reconstitute)
                .toList();
    }
}