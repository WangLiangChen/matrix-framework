package wang.liangchen.matrix.shop.product.domain.aggregate.producttype;

import wang.liangchen.matrix.shop.product.domain.aggregate.attribute.AttributeId;
import wang.liangchen.matrix.shop.product.domain.shared.DomainException;

import java.time.Instant;
import java.util.*;

/** Immutable published schema snapshot. A SPU pins to one exact version. */
public record ProductTypeVersion(ProductTypeId productTypeId, int version, Instant publishedAt,
                                 List<ProductTypeAttribute> attributes) {
    public ProductTypeVersion {
        Objects.requireNonNull(productTypeId); Objects.requireNonNull(publishedAt);
        if (version < 1) throw new IllegalArgumentException("version must be >= 1");
        attributes = List.copyOf(attributes);
        Set<Object> ids = new HashSet<>(); Set<String> codes = new HashSet<>();
        for (ProductTypeAttribute attribute : attributes) {
            if (!ids.add(attribute.attributeId())) throw new DomainException("Duplicate ProductType attribute id");
            if (!codes.add(attribute.attributeCode())) throw new DomainException("Duplicate ProductType attribute code");
        }
    }
    public Optional<ProductTypeAttribute> findById(AttributeId id) {
        return attributes.stream().filter(a -> a.attributeId().equals(id)).findFirst();
    }
    public Optional<ProductTypeAttribute> findByCode(String code) {
        return attributes.stream().filter(a -> a.attributeCode().equals(code)).findFirst();
    }
    public List<ProductTypeAttribute> attributesFor(ProductTypeAttribute.Scope scope) {
        return attributes.stream().filter(a -> a.scope() == scope).toList();
    }
}
