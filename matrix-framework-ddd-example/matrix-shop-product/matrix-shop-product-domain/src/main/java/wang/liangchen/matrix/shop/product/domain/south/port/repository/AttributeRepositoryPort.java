package wang.liangchen.matrix.shop.product.domain.south.port.repository;
import wang.liangchen.matrix.shop.product.domain.aggregate.attribute.Attribute; import wang.liangchen.matrix.shop.product.domain.aggregate.attribute.AttributeId; import java.util.Optional;
public interface AttributeRepositoryPort { Optional<Attribute> findById(AttributeId id); }