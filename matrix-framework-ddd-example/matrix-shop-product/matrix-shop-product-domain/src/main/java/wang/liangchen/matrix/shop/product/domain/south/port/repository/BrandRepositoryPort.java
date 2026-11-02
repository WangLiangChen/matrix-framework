package wang.liangchen.matrix.shop.product.domain.south.port.repository;
import wang.liangchen.matrix.shop.product.domain.aggregate.brand.Brand; import wang.liangchen.matrix.shop.product.domain.aggregate.brand.BrandId; import java.util.Optional;
public interface BrandRepositoryPort { Optional<Brand> findById(BrandId id); }