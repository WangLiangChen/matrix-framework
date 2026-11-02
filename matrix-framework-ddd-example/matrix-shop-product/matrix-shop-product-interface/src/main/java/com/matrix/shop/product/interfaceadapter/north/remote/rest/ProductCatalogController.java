package wang.liangchen.matrix.product.interfaceadapter.north.remote.rest;

import wang.liangchen.matrix.product.application.command.*;
import wang.liangchen.matrix.product.application.usecase.*;
import wang.liangchen.matrix.product.contract.message.request.*;
import wang.liangchen.matrix.product.contract.message.response.ResourceCreatedResponse;
import wang.liangchen.matrix.product.domain.aggregate.sku.*;
import wang.liangchen.matrix.product.domain.aggregate.spu.*;
import wang.liangchen.matrix.product.domain.shared.id.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}")
public class ProductCatalogController {
    private final CreateSpuUseCase createSpu; private final CreateSkuUseCase createSku;
    public ProductCatalogController(CreateSpuUseCase createSpu, CreateSkuUseCase createSku) { this.createSpu = createSpu; this.createSku = createSku; }

    @PostMapping("/spus") @ResponseStatus(HttpStatus.CREATED)
    public ResourceCreatedResponse createSpu(@PathVariable UUID tenantId, @RequestBody CreateSpuRequest request) {
        List<AttributeValue> attrs = request.attributes().stream().map(AttributeValueRequestMapper::toDomain).toList();
        List<SpuCategoryAssignment> categories = request.categories().stream().map(c -> new SpuCategoryAssignment(new CategoryId(c.categoryId()), c.primary(), null)).toList();
        List<MediaReference> media = request.media().stream().map(m -> new MediaReference(m.mediaId(), m.role(), m.altText(), m.sortOrder())).toList();
        List<VariantDimension> dimensions = request.variantDimensions().stream().map(d -> new VariantDimension(new AttributeId(d.attributeId()), d.attributeCode(), d.optionCodes(), d.displayOrder())).toList();
        CreateSpuCommand command = new CreateSpuCommand(new TenantId(tenantId), new SpuCode(request.spuCode()), new ProductTypeId(request.productTypeId()),
                request.productTypeVersion(), request.brandId() == null ? null : new BrandId(request.brandId()), request.name(), request.subtitle(),
                request.description(), attrs, categories, media, new VariantConfiguration(dimensions));
        SpuId id = createSpu.create(command); return new ResourceCreatedResponse(id.value(), "SPU");
    }

    @PostMapping("/spus/{spuId}/skus") @ResponseStatus(HttpStatus.CREATED)
    public ResourceCreatedResponse createSku(@PathVariable UUID tenantId, @PathVariable UUID spuId, @RequestBody CreateSkuRequest request) {
        List<AttributeValue> attrs = request.attributes().stream().map(AttributeValueRequestMapper::toDomain).toList();
        List<Barcode> barcodes = request.barcodes().stream().map(b -> new Barcode(b.type(), b.value())).toList();
        PackageSpecification pack = request.packageSpecification() == null ? null : new PackageSpecification(
                measure(request.packageSpecification().weight()), measure(request.packageSpecification().length()),
                measure(request.packageSpecification().width()), measure(request.packageSpecification().height()));
        List<MediaReference> media = request.media().stream().map(m -> new MediaReference(m.mediaId(), m.role(), m.altText(), m.sortOrder())).toList();
        SkuId id = createSku.create(new CreateSkuCommand(new TenantId(tenantId), new SpuId(spuId), new SkuCode(request.skuCode()), attrs, barcodes, pack, media));
        return new ResourceCreatedResponse(id.value(), "SKU");
    }
    private static Measure measure(MeasureRequest r) { return r == null ? null : new Measure(r.value(), r.unitCode()); }
}
