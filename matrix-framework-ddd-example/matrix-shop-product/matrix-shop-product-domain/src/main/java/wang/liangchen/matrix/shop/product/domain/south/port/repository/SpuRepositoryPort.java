package wang.liangchen.matrix.shop.product.domain.south.port.repository;
import wang.liangchen.matrix.shop.product.domain.aggregate.spu.Spu; import wang.liangchen.matrix.product.domain.shared.id.*; import java.util.Optional;
public interface SpuRepositoryPort { Spu save(Spu spu); Optional<Spu> findById(TenantId tenantId, SpuId id); boolean existsBySpuCode(TenantId tenantId, String spuCode); }