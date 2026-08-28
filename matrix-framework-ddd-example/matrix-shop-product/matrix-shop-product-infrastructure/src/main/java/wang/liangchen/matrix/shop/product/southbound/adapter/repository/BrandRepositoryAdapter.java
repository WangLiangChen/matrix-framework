package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.brand.Brand;
import wang.liangchen.matrix.shop.product.domain.brand.BrandFactory;
import wang.liangchen.matrix.shop.product.domain.brand.BrandId;
import wang.liangchen.matrix.shop.product.domain.port.BrandRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * 品牌仓储适配器：实现品牌仓储端口，完成品牌聚合与持久化对象之间的防腐翻译，
 * 重建聚合时委托品牌工厂的reconstitute方法；查询读侧返回聚合根。
 */
@Repository
@Adapter(PortType.Repository)
public class BrandRepositoryAdapter implements BrandRepositoryPort, IRepositoryAdapter {

    private final BrandDao brandDao;
    private final BrandFactory brandFactory = new BrandFactory();

    public BrandRepositoryAdapter(BrandDao brandDao) {
        this.brandDao = brandDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Brand> findById(BrandId brandId) {
        return brandDao.findById(brandId.value()).map(this::reconstitute);
    }

    @Override
    public void save(Brand brand) {
        brandDao.save(toPo(brand));
    }

    @Override
    public void remove(Brand brand) {
        brandDao.deleteById(brand.id().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Brand> findAll() {
        return brandDao.findAllByOrderByNameAsc().stream()
                .map(this::reconstitute)
                .toList();
    }

    private Brand reconstitute(BrandPo po) {
        return brandFactory.reconstitute(BrandId.of(po.getId()), po.getName(), po.getDescription(), po.getLogo());
    }

    private BrandPo toPo(Brand brand) {
        BrandPo po = new BrandPo();
        po.setId(brand.id().value());
        po.setName(brand.name());
        po.setDescription(brand.description());
        po.setLogo(brand.logo());
        return po;
    }
}
