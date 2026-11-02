package wang.liangchen.matrix.product.contract.message.request;
import java.util.List; import java.util.UUID;
public record CreateSpuRequest(String spuCode, UUID productTypeId, int productTypeVersion, UUID brandId,
        String name, String subtitle, String description, List<AttributeValueRequest> attributes,
        List<CategoryAssignmentRequest> categories, List<MediaReferenceRequest> media,
        List<VariantDimensionRequest> variantDimensions) {
    public CreateSpuRequest { attributes = attributes == null ? List.of() : List.copyOf(attributes); categories = categories == null ? List.of() : List.copyOf(categories); media = media == null ? List.of() : List.copyOf(media); variantDimensions = variantDimensions == null ? List.of() : List.copyOf(variantDimensions); }
}
