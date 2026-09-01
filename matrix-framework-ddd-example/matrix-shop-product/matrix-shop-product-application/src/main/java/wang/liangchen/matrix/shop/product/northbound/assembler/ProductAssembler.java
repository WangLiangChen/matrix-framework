package wang.liangchen.matrix.shop.product.northbound.assembler;

import wang.liangchen.matrix.framework.ddd.assembler.AbstractAssembler;
import wang.liangchen.matrix.framework.ddd.assembler.Assembler;
import wang.liangchen.matrix.shop.product.domain.product.AttributeValue;
import wang.liangchen.matrix.shop.product.domain.product.Product;
import wang.liangchen.matrix.shop.product.domain.product.SkuSummary;
import wang.liangchen.matrix.shop.product.message.response.ProductDetailView;
import wang.liangchen.matrix.shop.product.message.response.ProductView;
import wang.liangchen.matrix.shop.product.message.response.SkuView;

import java.util.List;

/**
 * 商品视图装配器：出站装配——将商品聚合装配为查询视图，
 * 只做字段映射与类型转换，不含业务规则。
 */
@Assembler
public class ProductAssembler extends AbstractAssembler {

    /**
     * 出站：商品聚合 → 商品视图。
     */
    public ProductView toProductView(Product product) {
        return new ProductView(product.id().value(), product.name(), product.subtitle(),
                product.categoryId().value(), product.brandId().value(), product.listed());
    }

    /**
     * 出站：商品聚合 → 商品明细视图。
     */
    public ProductDetailView toProductDetailView(Product product) {
        return new ProductDetailView(product.id().value(), product.name(), product.subtitle(),
                product.categoryId().value(), product.brandId().value(), product.listed(),
                attributeValues(product.attributeValues()),
                product.skuSummaries().stream().map(this::toSkuView).toList());
    }

    /**
     * 出站：SKU摘要 → SKU视图。
     */
    public SkuView toSkuView(SkuSummary sku) {
        return new SkuView(sku.id().value(), attributeValues(sku.attributeValues()), sku.price().amount(), sku.stock());
    }

    /**
     * 出站：属性值引用列表 → 属性值契约列表。
     */
    public List<wang.liangchen.matrix.shop.product.message.request.AttributeValue> attributeValues(List<AttributeValue> refs) {
        return refs.stream()
                .map(ref -> new wang.liangchen.matrix.shop.product.message.request.AttributeValue(ref.attributeId().value(), ref.value()))
                .toList();
    }
}
