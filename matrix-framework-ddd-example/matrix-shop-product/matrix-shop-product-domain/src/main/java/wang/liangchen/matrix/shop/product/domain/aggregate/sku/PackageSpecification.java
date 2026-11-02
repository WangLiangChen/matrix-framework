package wang.liangchen.matrix.shop.product.domain.aggregate.sku;
public record PackageSpecification(Measure weight, Measure length, Measure width, Measure height) {
    public PackageSpecification { if (length == null || width == null || height == null) throw new IllegalArgumentException("Package dimensions are required together"); }
}
