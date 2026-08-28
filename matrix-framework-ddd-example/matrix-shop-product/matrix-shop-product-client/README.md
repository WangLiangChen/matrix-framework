# matrix-shop-product-client

## 允许的依赖

- `matrix-shop-product-contract`
- `spring-web`（远程调用传输：RestClient）

## 不允许的依赖

- 任何业务模块：`domain`、`application`、`infrastructure`、`interface`、`bootstrap`

## 角色

客户端 SDK：供下游消费者在商品上下文独立部署为微服务时远程调用。与单体形态的对应关系：contract 的 `service` 包定义应用服务接口 `ProductQueryService`，单体部署时由应用服务本地实现（northbound.local），微服务部署时由本模块远程实现，调用方只依赖 contract（按需引入 client）即可不感知部署形态。

## 职责

- `ProductFeignClient`：调用独立部署的商品微服务开放主机服务（`ProductResource`）的远程客户端；命名沿用 Feign 语义（声明式远程调用），传输以 Spring RestClient 实现，未引入 Spring Cloud，保持技术栈最小
- `ProductFeignClientAdapter`：远程调用的适配器，实现 contract 中的应用服务接口 `ProductQueryService`，在方法中执行远程调用并转换参数与返回结果

## 核心约定

- 只依赖 `contract` 的发布语言，不感知商品上下文的内部实现
- 参数与返回结果的转换在适配器内完成，调用方只见消息契约
- 远程调用失败抛出的技术异常（如 `RestClientException`）由调用方的防腐层翻译，本模块不抛出商品上下文的领域异常
- 类为普通 Java 类（非 Spring 组件），由调用方自行装配（如以 `@Bean` 注册 `ProductFeignClient(url)` 与适配器）
