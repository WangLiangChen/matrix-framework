package wang.liangchen.matrix.shop.product.domain.aggregate.attribute;

public record AttributeOption(String code, String name, String swatch, boolean active) {
    public AttributeOption {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("option code is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("option name is required");
    }

    public AttributeOption deactivate() {
        return new AttributeOption(code, name, swatch, false);
    }
}
