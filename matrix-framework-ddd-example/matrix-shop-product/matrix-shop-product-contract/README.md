# matrix-shop-product-contract

## 允许的依赖

- `matrix-framework-ddd`（根 POM 统一注入的框架规范，消息契约元数据标注）

## 不允许的依赖

- 任何业务模块：`domain`、`application`、`infrastructure`、`interface`、`client`、`bootstrap`
- 任何技术实现框架（Spring、JPA/Hibernate 等）

## 角色

消息契约，本上下文的发布语言：声明客户端与应用服务之间、以及本上下文与下游限界上下文之间交互的消息类型（`message.request` 请求、`message.response` 结果/视图）与应用服务接口（`service` 包）。被 `interface`、`application`、`client` 引用，自身不依赖任何业务模块。

## 职责

- 定义命令请求（`*CommandRequest`，实现 `ICommandRequest`）与查询请求（`*QueryRequest`，实现 `IQueryRequest`）
- 定义用例结果（`*Result`，实现 `IResult`）与查询视图（`*View`，实现 `IView`）
- 以 `@MessageContract` 标注每个消息的北向方向（`MessageDirection.NORTHBOUND`）、类型（`COMMAND_REQUEST`/`QUERY_REQUEST`/`RESULT`/`VIEW` 等）与交换模式
- `ProductDetailView` 是面向下游订单上下文开放的发布语言
- `service` 包定义面向下游的应用服务接口（`ProductQueryService`）：方法只操作消息契约；本地实现位于应用层（northbound.local），远程实现位于 client 模块（`ProductFeignClientAdapter`）

## 核心约定

- 消息以 record 实现，字段只使用平台类型（String、BigDecimal、List 等），不暴露领域模型对象
- 标识以字符串形式跨边界传递（如 `productId`），转换为领域身份标识的职责在应用层
- 嵌套消息同样以 `@MessageContract` 标注（如 `CreateProductCommandRequest.Sku`）
- 模块只承载数据结构与接口声明，不包含任何业务逻辑
