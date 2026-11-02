package wang.liangchen.matrix.product.infrastructure.configuration;
import wang.liangchen.matrix.product.application.service.*; import wang.liangchen.matrix.product.application.usecase.*;
import wang.liangchen.matrix.shop.product.domain.south.port.publisher.DomainEventPublisherPort;
import wang.liangchen.matrix.product.domain.south.port.repository.*;
import org.springframework.context.annotation.*;
@Configuration
public class ProductApplicationServiceConfiguration {
    @Bean public CreateSpuUseCase createSpuUseCase(SpuRepositoryPort s, ProductTypeRepositoryPort t, BrandRepositoryPort b, CategoryRepositoryPort c, DomainEventPublisherPort e) { return new CreateSpuService(s,t,b,c,e); }
    @Bean public CreateSkuUseCase createSkuUseCase(SpuRepositoryPort s, SkuRepositoryPort k, ProductTypeRepositoryPort t, DomainEventPublisherPort e) { return new CreateSkuService(s,k,t,e); }
}
