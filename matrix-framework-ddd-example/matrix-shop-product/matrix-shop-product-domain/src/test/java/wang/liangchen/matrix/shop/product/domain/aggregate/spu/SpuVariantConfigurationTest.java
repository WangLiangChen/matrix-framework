package wang.liangchen.matrix.shop.product.domain.aggregate.spu;

import wang.liangchen.matrix.product.domain.aggregate.producttype.*;
import wang.liangchen.matrix.shop.product.domain.shared.attribute.AttributeDataType;
import wang.liangchen.matrix.product.domain.shared.id.*;
import org.junit.jupiter.api.Test;
import java.time.Instant; import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SpuVariantConfigurationTest {
    @Test void selectedVariantOptionsMustBeAllowedByProductType() {
        ProductTypeId typeId = ProductTypeId.newId(); AttributeId colorId = AttributeId.newId();
        ProductTypeAttribute color = new ProductTypeAttribute(colorId, "color", AttributeDataType.ENUM,
                ProductTypeAttribute.Cardinality.SINGLE, ProductTypeAttribute.Scope.SKU,
                ProductTypeAttribute.Role.VARIANT_DIMENSION, true, true, true, true, false, 1, List.of("RED", "BLACK"));
        ProductTypeVersion schema = new ProductTypeVersion(typeId, 1, Instant.now(), List.of(color));
        VariantConfiguration config = new VariantConfiguration(List.of(new VariantDimension(colorId, "color", List.of("RED"), 1)));
        assertDoesNotThrow(() -> config.validateAgainst(schema));
        VariantConfiguration invalid = new VariantConfiguration(List.of(new VariantDimension(colorId, "color", List.of("GREEN"), 1)));
        assertThrows(RuntimeException.class, () -> invalid.validateAgainst(schema));
    }
}
