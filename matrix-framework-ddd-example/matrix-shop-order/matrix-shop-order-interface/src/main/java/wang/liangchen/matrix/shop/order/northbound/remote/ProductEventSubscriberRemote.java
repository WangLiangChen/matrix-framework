package wang.liangchen.matrix.shop.order.northbound.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.liangchen.matrix.framework.ddd.northbound.remote.ISubscriberRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;
import wang.liangchen.matrix.shop.order.northbound.local.ProductEventApplicationService;
import wang.liangchen.matrix.shop.product.message.event.ProductDelistedEvent;

/**
 * 商品事件订阅者：订单上下文作为下游订阅商品上下文对外发布的事件契约（发布语言），
 * 是跨上下文协作的远程入口（开放主机服务的对偶），只操作消息契约，
 * 翻译为基本类型值后经事件应用服务完成用例（防腐层不引入上游领域模型）。
 * <p>
 * 单体部署：商品上下文经进程内事件总线广播事件契约，本订阅者事务提交后处理（最终一致）；
 * 微服务部署：消息总线将事件契约投递到HTTP端点。
 */
@RestController
@RequestMapping("/order-subscribers")
@Remote(RemoteType.Subscriber)
public class ProductEventSubscriberRemote implements ISubscriberRemote {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventSubscriberRemote.class);

    private final ProductEventApplicationService productEventApplicationService;

    public ProductEventSubscriberRemote(ProductEventApplicationService productEventApplicationService) {
        this.productEventApplicationService = productEventApplicationService;
    }

    /**
     * 订阅"商品已下架"（单体形态：进程内事件总线）。
     * 发布方（商品上下文）事务提交后处理，订阅方失败不影响已发布的领域事实。
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductDelisted(ProductDelistedEvent event) {
        handle(event);
    }

    /**
     * 订阅"商品已下架"（微服务形态：消息总线投递的远程入口）。
     */
    @PostMapping("/product-delisted")
    public void onProductDelistedRequest(@RequestBody ProductDelistedEvent event) {
        handle(event);
    }

    private void handle(ProductDelistedEvent event) {
        try {
            productEventApplicationService.onProductDelisted(event.getProductId());
        } catch (Exception ex) {
            LOGGER.error("处理商品下架事件失败：{}", event, ex);
        }
    }
}
