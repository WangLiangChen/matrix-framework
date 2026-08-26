package wang.liangchen.matrix.shop.product.southbound.adapter.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IRepositoryAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.shop.product.domain.attribute.AttributeId;
import wang.liangchen.matrix.shop.product.domain.brand.BrandId;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.port.ProductQueryPort;
import wang.liangchen.matrix.shop.product.domain.port.ProductRepositoryPort;
import wang.liangchen.matrix.shop.product.domain.product.*;
import wang.liangchen.matrix.shop.product.domain.readmodel.ProductDetail;
import wang.liangchen.matrix.shop.product.domain.readmodel.ProductSummary;
import wang.liangchen.matrix.shop.product.domain.readmodel.SkuSummary;

import java.util.List;
import java.util.Optional;

/**
 * 商品仓储适配器：实现商品仓储端口与查询端口，完成商品聚合与持久化对象之间的防腐翻译，
 * 重建聚合时委托商品工厂的reconstitute方法，持久化对象不向领域层泄漏。
 */
@Repository
@Adapter(PortType.Repository)
public class ProductRepositoryAdapter implements ProductRepositoryPort, ProductQueryPort, IRepositoryAdapter {

    private final ProductDao productDao;
    private final ProductFactory productFactory = new ProductFactory();

    public ProductRepositoryAdapter(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(ProductId productId) {
        return productDao.findById(productId.value()).map(this::reconstitute);
    }

    @Override
    public void save(Product product) {
        productDao.save(toPo(product));
    }

    @Override
    public void remove(Product product) {
        productDao.deleteById(product.id().value());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDetail> queryById(ProductId productId) {
        return productDao.findById(productId.value()).map(po -> new ProductDetail(
                ProductId.of(po.getId()), po.getName(), po.getSubtitle(),
                CategoryId.of(po.getCategoryId()), BrandId.of(po.getBrandId()), po.isListed(),
                po.getAttributeValues().stream().map(this::attributeValueRef).toList(),
                po.getSkus().stream().map(this::skuSummary).toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummary> queryProducts(String keyword, CategoryId categoryId) {
        return productDao.searchListed(keyword, categoryId == null ? null : categoryId.value()).stream()
                .map(po -> new ProductSummary(ProductId.of(po.getId()), po.getName(), po.getSubtitle(),
                        CategoryId.of(po.getCategoryId()), BrandId.of(po.getBrandId()), po.isListed()))
                .toList();
    }

    private Product reconstitute(ProductPo po) {
        return productFactory.reconstitute(
                ProductId.of(po.getId()), po.getName(), po.getSubtitle(),
                CategoryId.of(po.getCategoryId()), BrandId.of(po.getBrandId()),
                po.getAttributeValues().stream().map(this::attributeValueRef).toList(),
                po.getSkus().stream().map(this::skuSummary).toList(),
                po.isListed());
    }

    private ProductPo toPo(Product product) {
        ProductPo po = new ProductPo();
        po.setId(product.id().value());
        po.setName(product.name());
        po.setSubtitle(product.subtitle());
        po.setCategoryId(product.categoryId().value());
        po.setBrandId(product.brandId().value());
        po.setListed(product.listed());
        po.setAttributeValues(product.attributeValues().stream().map(this::attributeValueRefPo).toList());
        po.setSkus(product.skuSummaries().stream().map(this::skuPo).toList());
        return po;
    }

    private SkuSummary skuSummary(SkuPo po) {
        return new SkuSummary(SkuId.of(po.getId()),
                po.getAttributeValues().stream().map(this::attributeValueRef).toList(),
                Money.of(po.getPrice(), po.getCurrency()), po.getStock());
    }

    private SkuPo skuPo(SkuSummary summary) {
        SkuPo po = new SkuPo();
        po.setId(summary.id().value());
        po.setAttributeValues(summary.attributeValues().stream().map(this::attributeValueRefPo).toList());
        po.setPrice(summary.price().amount());
        po.setCurrency(summary.price().currency());
        po.setStock(summary.stock());
        return po;
    }

    private AttributeValueRef attributeValueRef(AttributeValueRefPo po) {
        return new AttributeValueRef(AttributeId.of(po.getAttributeId()), po.getValue());
    }

    private AttributeValueRefPo attributeValueRefPo(AttributeValueRef ref) {
        AttributeValueRefPo po = new AttributeValueRefPo();
        po.setAttributeId(ref.attributeId().value());
        po.setValue(ref.value());
        return po;
    }
}
