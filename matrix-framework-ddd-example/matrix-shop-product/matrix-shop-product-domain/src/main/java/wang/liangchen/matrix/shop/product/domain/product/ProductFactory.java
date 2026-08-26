package wang.liangchen.matrix.shop.product.domain.product;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.factory.IDomainFactory;
import wang.liangchen.matrix.shop.product.domain.brand.BrandId;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;
import wang.liangchen.matrix.shop.product.domain.readmodel.SkuSummary;

import java.util.List;

/**
 * 商品工厂：封装SPU与SKU的创建与重建逻辑。
 * 创建全新商品使用{@link #create}，从持久化数据重建聚合使用{@link #reconstitute}，
 * 重建由仓储适配器委托本工厂完成，工厂不访问仓储。
 */
@DomainModel(DomainMetaModel.DomainFactory)
public final class ProductFactory implements IDomainFactory {

    /**
     * 创建全新的商品聚合。
     */
    public Product create(String name, String subtitle, CategoryId categoryId, BrandId brandId,
                          List<AttributeValueRef> attributeValues, List<SkuTemplate> skuTemplates) {
        if (name == null || name.isBlank()) {
            throw new DomainException("商品名称不能为空");
        }
        List<Sku> skus = skuTemplates.stream()
                .map(template -> Sku.of(SkuId.generate(), template.attributeValues(), template.price(), template.stock()))
                .toList();
        Product product = new Product(ProductId.generate(), name, subtitle, categoryId, brandId, attributeValues, skus, false);
        product.created();
        return product;
    }

    /**
     * 从持久化数据重建商品聚合。
     */
    public Product reconstitute(ProductId id, String name, String subtitle, CategoryId categoryId, BrandId brandId,
                                List<AttributeValueRef> attributeValues, List<SkuSummary> skuSummaries, boolean listed) {
        List<Sku> skus = skuSummaries.stream()
                .map(summary -> Sku.of(summary.id(), summary.attributeValues(), summary.price(), summary.stock()))
                .toList();
        return new Product(id, name, subtitle, categoryId, brandId, attributeValues, skus, listed);
    }
}
