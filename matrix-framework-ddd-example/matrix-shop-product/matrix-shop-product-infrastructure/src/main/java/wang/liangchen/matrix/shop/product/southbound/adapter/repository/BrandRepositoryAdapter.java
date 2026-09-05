package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.AbstractRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.brand.Brand;
import wang.liangchen.matrix.shop.product.domain.brand.BrandId;
import wang.liangchen.matrix.shop.product.southbound.port.BrandRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * 品牌仓储适配器：实现品牌仓储端口，完成品牌聚合与持久化对象之间的防腐翻译，
 * 重建聚合时委托品牌聚合自身的reconstitute静态方法；查询读侧返回聚合根。
 */
@Repository
@Adapter(PortType.Repository)
public class BrandRepositoryAdapter extends AbstractRepositoryAdapter<BrandId, Brand, BrandPo> implements BrandRepositoryPort {

    private final BrandDao brandDao;

    public BrandRepositoryAdapter(BrandDao brandDao) {
        this.brandDao = brandDao;
    }

    @Override
    protected Optional<BrandPo> doFindById(BrandId id) {
        return brandDao.findById(id.value());
    }

    @Override
    protected void doSave(BrandPo po) {
        brandDao.save(po);
    }

    @Override
    protected void doRemoveById(BrandId id) {
        brandDao.deleteById(id.value());
    }

    @Override
    protected Brand reconstitute(BrandPo po) {
        return Brand.reconstitute(BrandId.of(po.getId()), po.getName(), po.getDescription(), po.getLogo());
    }

    @Override
    protected BrandPo toPo(Brand brand) {
        BrandPo po = new BrandPo();
        po.setId(brand.id().value());
        po.setName(brand.name());
        po.setDescription(brand.description());
        po.setLogo(brand.logo());
        return po;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Brand> findAll() {
        return brandDao.findAllByOrderByNameAsc().stream()
                .map(this::reconstitute)
                .toList();
    }
}