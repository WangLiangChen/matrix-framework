package wang.liangchen.matrix.shop.product.domain.aggregate.sku;

import wang.liangchen.matrix.product.domain.aggregate.spu.*;
import wang.liangchen.matrix.shop.product.domain.shared.attribute.AttributeValue;
import java.util.*;

/** Deterministic identity key for one SKU combination within an SPU. */
public record VariantKey(String value) {
    public VariantKey { if (value == null || value.isBlank()) throw new IllegalArgumentException("variant key required"); }
    public static VariantKey from(VariantConfiguration config, Collection<AttributeValue> values) {
        Map<Object, AttributeValue> byId = new HashMap<>(); values.forEach(v -> byId.put(v.attributeId(), v));
        List<String> parts = new ArrayList<>();
        for (VariantDimension dim : config.orderedDimensions()) {
            AttributeValue value = byId.get(dim.attributeId());
            if (!(value instanceof AttributeValue.EnumOption option)) throw new IllegalArgumentException("Missing variant dimension: " + dim.attributeCode());
            parts.add(dim.attributeCode() + "=" + option.optionCode());
        }
        return new VariantKey(parts.isEmpty() ? "_DEFAULT" : String.join("|", parts));
    }
}
