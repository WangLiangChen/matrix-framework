package wang.liangchen.matrix.product.application.service;

import wang.liangchen.matrix.product.application.command.CreateSpuCommand;
import wang.liangchen.matrix.product.application.usecase.CreateSpuUseCase;
import wang.liangchen.matrix.shop.product.domain.aggregate.brand.Brand;
import wang.liangchen.matrix.shop.product.domain.aggregate.category.Category;
import wang.liangchen.matrix.shop.product.domain.aggregate.producttype.ProductTypeVersion;
import wang.liangchen.matrix.shop.product.domain.aggregate.spu.Spu;
import wang.liangchen.matrix.shop.product.domain.shared.DomainException;
import wang.liangchen.matrix.shop.product.domain.aggregate.spu.SpuId;
import wang.liangchen.matrix.shop.product.domain.south.port.publisher.DomainEventPublisherPort;
import wang.liangchen.matrix.product.domain.south.port.repository.*;
import org.springframework.transaction.annotation.Transactional;

public class CreateSpuService implements CreateSpuUseCase {
    private final SpuRepositoryPort spuRepository; private final ProductTypeRepositoryPort productTypeRepository;
    private final BrandRepositoryPort brandRepository; private final CategoryRepositoryPort categoryRepository;
    private final DomainEventPublisherPort eventPublisher;
    public CreateSpuService(SpuRepositoryPort spuRepository, ProductTypeRepositoryPort productTypeRepository,
            BrandRepositoryPort brandRepository, CategoryRepositoryPort categoryRepository, DomainEventPublisherPort eventPublisher) {
        this.spuRepository = spuRepository; this.productTypeRepository = productTypeRepository; this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository; this.eventPublisher = eventPublisher;
    }
    @Override @Transactional
    public SpuId create(CreateSpuCommand command) {
        if (spuRepository.existsBySpuCode(command.tenantId(), command.spuCode().value())) throw new DomainException("SPU code already exists");
        ProductTypeVersion schema = productTypeRepository.findPublishedVersion(command.productTypeId(), command.productTypeVersion())
                .orElseThrow(() -> new DomainException("Published ProductType version not found"));
        if (command.brandId() != null) {
            Brand brand = brandRepository.findById(command.brandId()).orElseThrow(() -> new DomainException("Brand not found")); brand.assertUsable();
        }
        for (var assignment : command.categories()) {
            Category category = categoryRepository.findById(assignment.categoryId()).orElseThrow(() -> new DomainException("Category not found"));
            category.assertUsable(); category.assertAllows(command.productTypeId());
        }
        Spu spu = Spu.create(command.tenantId(), command.spuCode(), schema, command.brandId(), command.name(), command.subtitle(),
                command.description(), command.attributes(), command.categories(), command.media(), command.variantConfiguration());
        spuRepository.save(spu); eventPublisher.publish(spu.pullDomainEvents()); return spu.id();
    }
}
