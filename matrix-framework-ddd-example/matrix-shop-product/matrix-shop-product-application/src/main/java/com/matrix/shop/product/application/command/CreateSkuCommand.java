package wang.liangchen.matrix.product.application.command;
import wang.liangchen.matrix.product.domain.aggregate.sku.*; import wang.liangchen.matrix.shop.product.domain.aggregate.spu.MediaReference;
import wang.liangchen.matrix.shop.product.domain.shared.attribute.AttributeValue; import wang.liangchen.matrix.product.domain.shared.id.*; import java.util.*;
public record CreateSkuCommand(TenantId tenantId, SpuId spuId, SkuCode skuCode, List<AttributeValue> attributes,
        List<Barcode> barcodes, PackageSpecification packageSpecification, List<MediaReference> media) {
    public CreateSkuCommand { attributes = List.copyOf(attributes == null ? List.of() : attributes); barcodes = List.copyOf(barcodes == null ? List.of() : barcodes); media = List.copyOf(media == null ? List.of() : media); }
}
