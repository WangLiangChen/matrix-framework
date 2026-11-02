package wang.liangchen.matrix.product.contract.message.request;
import java.util.List; import java.util.UUID;
public record VariantDimensionRequest(UUID attributeId, String attributeCode, List<String> optionCodes, int displayOrder) {
    public VariantDimensionRequest { optionCodes = List.copyOf(optionCodes); }
}
