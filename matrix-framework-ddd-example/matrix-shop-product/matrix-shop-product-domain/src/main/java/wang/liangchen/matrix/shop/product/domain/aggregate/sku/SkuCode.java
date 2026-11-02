package wang.liangchen.matrix.shop.product.domain.aggregate.sku;
public record SkuCode(String value) { public SkuCode { if (value == null || value.isBlank()) throw new IllegalArgumentException("SKU code required"); } }
