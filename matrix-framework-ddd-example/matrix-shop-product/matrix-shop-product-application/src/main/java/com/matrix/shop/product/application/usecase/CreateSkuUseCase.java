package wang.liangchen.matrix.product.application.usecase;
import wang.liangchen.matrix.product.application.command.CreateSkuCommand; import wang.liangchen.matrix.shop.product.domain.aggregate.sku.SkuId;
public interface CreateSkuUseCase { SkuId create(CreateSkuCommand command); }
