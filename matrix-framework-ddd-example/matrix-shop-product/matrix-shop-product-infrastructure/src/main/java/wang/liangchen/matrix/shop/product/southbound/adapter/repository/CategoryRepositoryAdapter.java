package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.category.Category;
import wang.liangchen.matrix.shop.product.domain.category.CategoryFactory;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.port.CategoryRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * 类目仓储适配器：实现类目仓储端口，完成类目聚合与持久化对象之间的防腐翻译，
 * 重建聚合时委托类目工厂的reconstitute方法；查询读侧返回聚合根，类目树由应用层装配。
 */
@Repository
@Adapter(PortType.Repository)
public class CategoryRepositoryAdapter implements CategoryRepositoryPort, IRepositoryAdapter {

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
    public List<Category> findAll() {
        return categoryDao.findAllByOrderByNameAsc().stream()
                .map(this::reconstitute)
                .toList();
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
