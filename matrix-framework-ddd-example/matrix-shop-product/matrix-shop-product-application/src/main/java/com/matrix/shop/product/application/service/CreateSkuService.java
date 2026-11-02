package wang.liangchen.matrix.product.application.service;

import wang.liangchen.matrix.product.application.command.CreateSkuCommand;
import wang.liangchen.matrix.product.application.usecase.CreateSkuUseCase;
import wang.liangchen.matrix.shop.product.domain.aggregate.sku.Sku;
import wang.liangchen.matrix.shop.product.domain.aggregate.spu.Spu;
import wang.liangchen.matrix.shop.product.domain.aggregate.producttype.ProductTypeVersion;
import wang.liangchen.matrix.shop.product.domain.shared.DomainException;
import wang.liangchen.matrix.shop.product.domain.aggregate.sku.SkuId;
import wang.liangchen.matrix.shop.product.domain.south.port.publisher.DomainEventPublisherPort;
import wang.liangchen.matrix.product.domain.south.port.repository.*;
import org.springframework.transaction.annotation.Transactional;

public class CreateSkuService implements CreateSkuUseCase {
    private final SpuRepositoryPort spuRepository; private final SkuRepositoryPort skuRepository;
    private final ProductTypeRepositoryPort productTypeRepository; private final DomainEventPublisherPort eventPublisher;
    public CreateSkuService(SpuRepositoryPort spuRepository, SkuRepositoryPort skuRepository,
            ProductTypeRepositoryPort productTypeRepository, DomainEventPublisherPort eventPublisher) {
        this.spuRepository = spuRepository; this.skuRepository = skuRepository; this.productTypeRepository = productTypeRepository; this.eventPublisher = eventPublisher;
    }
    @Override @Transactional
    public SkuId create(CreateSkuCommand command) {
        if (skuRepository.existsBySkuCode(command.tenantId(), command.skuCode().value())) throw new DomainException("SKU code already exists");
        Spu spu = spuRepository.findById(command.tenantId(), command.spuId()).orElseThrow(() -> new DomainException("SPU not found"));
        ProductTypeVersion schema = productTypeRepository.findPublishedVersion(spu.productTypeId(), spu.productTypeVersion())
                .orElseThrow(() -> new DomainException("SPU pinned ProductType version not found"));
        Sku sku = Sku.create(spu, schema, command.skuCode(), command.attributes(), command.barcodes(), command.packageSpecification(), command.media());
        if (skuRepository.existsBySpuAndVariantKey(command.tenantId(), command.spuId(), sku.variantKey().value()))
            throw new DomainException("SKU variant combination already exists");
        skuRepository.save(sku); eventPublisher.publish(sku.pullDomainEvents()); return sku.id();
    }
}
