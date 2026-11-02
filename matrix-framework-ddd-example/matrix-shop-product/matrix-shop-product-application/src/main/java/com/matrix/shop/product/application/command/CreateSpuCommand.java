package wang.liangchen.matrix.product.application.command;
import wang.liangchen.matrix.product.domain.aggregate.spu.*; import wang.liangchen.matrix.shop.product.domain.shared.attribute.AttributeValue;
import wang.liangchen.matrix.product.domain.shared.id.*; import java.util.*;
public record CreateSpuCommand(TenantId tenantId, SpuCode spuCode, ProductTypeId productTypeId, int productTypeVersion,
        BrandId brandId, String name, String subtitle, String description, List<AttributeValue> attributes,
        List<SpuCategoryAssignment> categories, List<MediaReference> media, VariantConfiguration variantConfiguration) {
    public CreateSpuCommand { attributes = List.copyOf(attributes == null ? List.of() : attributes); categories = List.copyOf(categories == null ? List.of() : categories); media = List.copyOf(media == null ? List.of() : media); }
}
