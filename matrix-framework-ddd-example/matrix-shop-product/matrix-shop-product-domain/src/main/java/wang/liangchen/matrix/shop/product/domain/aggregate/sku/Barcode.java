package wang.liangchen.matrix.shop.product.domain.aggregate.sku;
public record Barcode(String type, String value) {
    public Barcode { if (type == null || type.isBlank() || value == null || value.isBlank()) throw new IllegalArgumentException("barcode type and value required"); }
}
