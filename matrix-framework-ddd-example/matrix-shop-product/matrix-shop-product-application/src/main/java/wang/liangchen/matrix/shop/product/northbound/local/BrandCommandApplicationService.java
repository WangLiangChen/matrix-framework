package wang.liangchen.matrix.shop.product.northbound.local;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.ICommandApplicationService;
import wang.liangchen.matrix.shop.product.domain.brand.Brand;
import wang.liangchen.matrix.shop.product.domain.brand.BrandId;
import wang.liangchen.matrix.shop.product.domain.exception.DomainException;
import wang.liangchen.matrix.shop.product.southbound.port.BrandRepositoryPort;
import wang.liangchen.matrix.shop.product.southbound.port.DomainEventPublisherPort;
import wang.liangchen.matrix.shop.product.message.request.CreateBrandCommandRequest;
import wang.liangchen.matrix.shop.product.message.request.RenameBrandCommandRequest;
import wang.liangchen.matrix.shop.product.message.response.CreateBrandResult;
import wang.liangchen.matrix.shop.product.message.response.RenameBrandResult;
import wang.liangchen.matrix.shop.product.northbound.exception.ApplicationException;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 品牌命令应用服务：编排品牌聚合实现命令用例。
 */
@Service
@ApplicationService(ApplicationServiceType.COMMAND)
public class BrandCommandApplicationService implements ICommandApplicationService {

    private final BrandRepositoryPort brandRepository;
    private final DomainEventPublisherPort eventPublisher;

    public BrandCommandApplicationService(BrandRepositoryPort brandRepository, DomainEventPublisherPort eventPublisher) {
        this.brandRepository = brandRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 用例：创建品牌。
     */
    @Transactional
    public CreateBrandResult createBrand(CreateBrandCommandRequest request) {
        return useCase("创建品牌", () -> {
            Brand brand = Brand.create(request.name(), request.description(), request.logo());
            brandRepository.save(brand);
            eventPublisher.publish(brand.events());
            brand.clearEvents();
            return new CreateBrandResult(brand.id().value());
        });
    }

    /**
     * 用例：重命名品牌。
     */
    @Transactional
    public RenameBrandResult renameBrand(RenameBrandCommandRequest request) {
        return useCase("重命名品牌", () -> {
            Brand brand = mutate(BrandId.of(request.brandId()), b -> b.rename(request.name()));
            return new RenameBrandResult(brand.id().value(), brand.name());
        });
    }

    private Brand mutate(BrandId brandId, Consumer<Brand> mutation) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new DomainException("品牌不存在：" + brandId.value()));
        mutation.accept(brand);
        brandRepository.save(brand);
        eventPublisher.publish(brand.events());
        brand.clearEvents();
        return brand;
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}