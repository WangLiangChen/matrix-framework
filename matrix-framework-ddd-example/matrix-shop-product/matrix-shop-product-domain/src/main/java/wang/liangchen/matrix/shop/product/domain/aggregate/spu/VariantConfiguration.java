package wang.liangchen.matrix.shop.product.domain.aggregate.spu;

import wang.liangchen.matrix.shop.product.domain.shared.DomainException;
import wang.liangchen.matrix.product.domain.aggregate.producttype.*;
import java.util.*;

/** Allowed variant space for one SPU; it is not a collection of actual SKUs. */
public record VariantConfiguration(List<VariantDimension> dimensions) {
    public VariantConfiguration {
        dimensions = List.copyOf(dimensions == null ? List.of() : dimensions);
        Set<Object> ids = new HashSet<>();
        for (VariantDimension d : dimensions) if (!ids.add(d.attributeId())) throw new DomainException("Duplicate variant dimension: " + d.attributeCode());
    }
    public static VariantConfiguration none() { return new VariantConfiguration(List.of()); }
    public void validateAgainst(ProductTypeVersion schema) {
        for (VariantDimension dimension : dimensions) {
            ProductTypeAttribute def = schema.findById(dimension.attributeId()).orElseThrow(() -> new DomainException("Unknown variant dimension: " + dimension.attributeCode()));
            if (def.scope() != ProductTypeAttribute.Scope.SKU || def.role() != ProductTypeAttribute.Role.VARIANT_DIMENSION)
                throw new DomainException("Attribute is not a configured SKU variant dimension: " + dimension.attributeCode());
            if (!def.attributeCode().equals(dimension.attributeCode())) throw new DomainException("Variant dimension code mismatch");
            if (!def.allowedOptionCodes().isEmpty() && !def.allowedOptionCodes().containsAll(dimension.optionCodes()))
                throw new DomainException("SPU selected options outside ProductType allow-list: " + dimension.attributeCode());
        }
        for (ProductTypeAttribute def : schema.attributes()) {
            if (def.scope() == ProductTypeAttribute.Scope.SKU && def.role() == ProductTypeAttribute.Role.VARIANT_DIMENSION && def.required()
                    && dimensions.stream().noneMatch(d -> d.attributeId().equals(def.attributeId())))
                throw new DomainException("Required variant dimension missing in SPU: " + def.attributeCode());
        }
    }
    public List<VariantDimension> orderedDimensions() { return dimensions.stream().sorted(Comparator.comparingInt(VariantDimension::displayOrder)).toList(); }
}
