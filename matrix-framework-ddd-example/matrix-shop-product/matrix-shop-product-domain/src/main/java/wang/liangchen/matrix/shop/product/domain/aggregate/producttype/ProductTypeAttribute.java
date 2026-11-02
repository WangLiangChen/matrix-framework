package wang.liangchen.matrix.shop.product.domain.aggregate.producttype;

import wang.liangchen.matrix.shop.product.domain.shared.attribute.AttributeDataType;
import wang.liangchen.matrix.shop.product.domain.aggregate.attribute.AttributeId;
import java.util.List;
import java.util.Objects;

/** Immutable usage rule snapshot for one attribute inside a ProductType version. */
public record ProductTypeAttribute(
        AttributeId attributeId, String attributeCode, AttributeDataType dataType,
        Cardinality cardinality, Scope scope, Role role, boolean required,
        boolean searchable, boolean filterable, boolean facetable, boolean sortable,
        int displayOrder, List<String> allowedOptionCodes) {
    public enum Scope { SPU, SKU }
    public enum Role { NORMAL, VARIANT_DIMENSION }
    public enum Cardinality { SINGLE, MULTIPLE }
    public ProductTypeAttribute {
        Objects.requireNonNull(attributeId); require(attributeCode); Objects.requireNonNull(dataType);
        Objects.requireNonNull(cardinality); Objects.requireNonNull(scope); Objects.requireNonNull(role);
        allowedOptionCodes = List.copyOf(allowedOptionCodes == null ? List.of() : allowedOptionCodes);
        if (role == Role.VARIANT_DIMENSION && (scope != Scope.SKU || dataType != AttributeDataType.ENUM || cardinality != Cardinality.SINGLE))
            throw new IllegalArgumentException("VARIANT_DIMENSION must be a single-valued SKU ENUM attribute");
        if (displayOrder < 0) throw new IllegalArgumentException("displayOrder must be >= 0");
    }
    private static void require(String v) { if (v == null || v.isBlank()) throw new IllegalArgumentException("attributeCode required"); }
}
