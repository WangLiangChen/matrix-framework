package wang.liangchen.matrix.shop.product.domain.shared.attribute;

import wang.liangchen.matrix.shop.product.domain.aggregate.producttype.ProductTypeAttribute;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class AttributeValueValidator {
    private AttributeValueValidator() {}

    public static void validate(AttributeValue value, ProductTypeAttribute definition) {
        if (!value.attributeId().equals(definition.attributeId()))
            throw new IllegalArgumentException("Attribute id does not match ProductType definition");
        if (value.dataType() != definition.dataType())
            throw new IllegalArgumentException("Attribute data type mismatch for " + definition.attributeCode());
        Set<String> selected = optionCodes(value);
        if (!selected.isEmpty()) {
            if (definition.dataType() != AttributeDataType.ENUM && definition.dataType() != AttributeDataType.ENUM_SET)
                throw new IllegalArgumentException("Option values supplied for non-enum attribute " + definition.attributeCode());
            if (!definition.allowedOptionCodes().isEmpty() && !definition.allowedOptionCodes().containsAll(selected))
                throw new IllegalArgumentException("Option not allowed by ProductType for " + definition.attributeCode());
        }
        if (definition.role() == ProductTypeAttribute.Role.VARIANT_DIMENSION
                && (definition.dataType() != AttributeDataType.ENUM || definition.cardinality() != ProductTypeAttribute.Cardinality.SINGLE))
            throw new IllegalArgumentException("Variant dimension must be a single-valued ENUM: " + definition.attributeCode());
    }

    public static Set<String> optionCodes(AttributeValue value) {
        if (value instanceof AttributeValue.EnumOption e) return Set.of(e.optionCode());
        if (value instanceof AttributeValue.EnumOptions e) return new HashSet<>(e.optionCodes());
        return Set.of();
    }

    public static void validateRequired(Collection<ProductTypeAttribute> definitions, Collection<AttributeValue> values,
                                        ProductTypeAttribute.Scope scope) {
        Set<java.util.UUID> present = new HashSet<>();
        for (AttributeValue value : values) present.add(value.attributeId().value());
        definitions.stream().filter(d -> d.scope() == scope && d.required()).forEach(d -> {
            if (!present.contains(d.attributeId().value()))
                throw new IllegalArgumentException("Required attribute missing: " + d.attributeCode());
        });
    }
}
