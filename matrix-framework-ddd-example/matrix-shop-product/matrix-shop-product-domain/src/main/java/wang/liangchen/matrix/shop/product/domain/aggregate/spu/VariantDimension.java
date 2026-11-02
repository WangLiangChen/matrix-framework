package wang.liangchen.matrix.shop.product.domain.aggregate.spu;

import wang.liangchen.matrix.shop.product.domain.aggregate.attribute.AttributeId;
import java.util.*;

/** SPU's chosen values for one SKU dimension. */
public record VariantDimension(AttributeId attributeId, String attributeCode, List<String> optionCodes, int displayOrder) {
    public VariantDimension {
        Objects.requireNonNull(attributeId);
        if (attributeCode == null || attributeCode.isBlank()) throw new IllegalArgumentException("attributeCode required");
        optionCodes = List.copyOf(optionCodes);
        if (optionCodes.isEmpty()) throw new IllegalArgumentException("At least one selected option is required");
        if (new HashSet<>(optionCodes).size() != optionCodes.size()) throw new IllegalArgumentException("Duplicate option code");
        if (displayOrder < 0) throw new IllegalArgumentException("displayOrder must be >= 0");
    }
}
