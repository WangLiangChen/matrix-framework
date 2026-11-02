package wang.liangchen.matrix.product.contract.message.request;
import java.util.List;
public record CreateSkuRequest(String skuCode, List<AttributeValueRequest> attributes, List<BarcodeRequest> barcodes,
        PackageSpecificationRequest packageSpecification, List<MediaReferenceRequest> media) {
    public CreateSkuRequest { attributes = attributes == null ? List.of() : List.copyOf(attributes); barcodes = barcodes == null ? List.of() : List.copyOf(barcodes); media = media == null ? List.of() : List.copyOf(media); }
}
