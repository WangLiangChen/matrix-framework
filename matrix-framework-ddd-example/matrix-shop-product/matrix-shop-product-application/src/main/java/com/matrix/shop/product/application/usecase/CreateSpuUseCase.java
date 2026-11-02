package wang.liangchen.matrix.product.application.usecase;
import wang.liangchen.matrix.product.application.command.CreateSpuCommand; import wang.liangchen.matrix.shop.product.domain.aggregate.spu.SpuId;
public interface CreateSpuUseCase { SpuId create(CreateSpuCommand command); }
