# matrix-shop-product-interfaces

## 允许的依赖

- `matrix-shop-product-application`
- `matrix-shop-product-contract`（经 application 传递）
- `matrix-framework-ddd`（根 POM 统一注入的框架规范）

## 不允许的依赖

- `matrix-shop-product-domain`
- `matrix-shop-product-infrastructure`
- `matrix-shop-product-client`
- `matrix-shop-product-bootstrap`

## 角色

北向远程层（`northbound.remote`）：将应用服务暴露为 HTTP API（spring-boot-starter-web），是客户端与下游限界上下文进入本上下文的入口。

## 职责

- `*Controller`（`@Remote(RemoteType.Controller)`，实现 `IControllerRemote`）：面向 UI 的远程服务，只操作消息契约（请求/结果/视图），通过应用服务完成用例编排
- `ProductResource`（`@Remote(RemoteType.Resource)`，实现 `IResourceRemote`）：面向下游限界上下文的开放主机服务，以发布语言 `ProductDetailView` 服务下游（订单上下文的防腐层经此获取商品快照）
- `ProductApplicationExceptionHandler`：将应用异常转换为客户端可读的 HTTP 响应（400 Bad Request）

## 核心约定

- 远程服务只调用应用服务，不直接访问领域对象、聚合或端口
- 入参与返回均为 contract 中的消息类型，领域模型不越过应用层
- 应用异常统一由 `@RestControllerAdvice`（限定 `northbound.remote` 包）处理，不向客户端泄漏堆栈
