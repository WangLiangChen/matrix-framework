package wang.liangchen.matrix.shop.product.domain.south.port.repository;
import wang.liangchen.matrix.shop.product.domain.aggregate.category.Category; import wang.liangchen.matrix.shop.product.domain.aggregate.category.CategoryId; import java.util.Optional;
public interface CategoryRepositoryPort { Optional<Category> findById(CategoryId id); }