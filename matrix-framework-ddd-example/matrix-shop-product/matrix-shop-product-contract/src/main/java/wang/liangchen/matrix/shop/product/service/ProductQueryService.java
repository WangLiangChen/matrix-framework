package wang.liangchen.matrix.shop.product.service;

import wang.liangchen.matrix.shop.product.message.request.ProductQueryRequest;
import wang.liangchen.matrix.shop.product.message.response.ProductDetailView;

/**
 * 商品查询服务接口：商品上下文面向下游消费者的应用服务契约（发布语言的接口形式），
 * 方法只操作消息契约，不暴露领域模型。
 * 单体部署时由应用服务本地实现（northbound.local），微服务部署时由 client 模块的
 * 远程适配器实现，下游只依赖本接口（按需引入 client）即可不感知部署形态。
 */
public interface ProductQueryService {

    /**
     * 用例：查询商品明细（开放主机服务发布的商品快照）。
     */
    ProductDetailView queryProductDetail(ProductQueryRequest request);
}
