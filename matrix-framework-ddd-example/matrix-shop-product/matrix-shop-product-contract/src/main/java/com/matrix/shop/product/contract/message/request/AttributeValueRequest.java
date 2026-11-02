package wang.liangchen.matrix.product.contract.message.request;
import java.util.List; import java.util.UUID;
public record AttributeValueRequest(UUID attributeId, String attributeCode, String dataType, String value, List<String> values, String unitCode) {
    public AttributeValueRequest { values = values == null ? List.of() : List.copyOf(values); }
}
