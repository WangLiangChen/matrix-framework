# matrix-shop-product-client

## 允许的依赖

- `matrix-shop-product-contract`

## 不允许的依赖

- 任何业务模块：`domain`、`application`、`infrastructure`、`interfaces`、`bootstrap`

## 角色

客户端 SDK：供下游限界上下文在商品上下文独立部署为微服务时远程调用。当前为 POC 骨架，随独立部署模式落地而补全。

## 职责

- `ProductFeignClient`：调用独立部署的商品微服务的远程客户端
- `ProductFeignClientAdapter`：远程调用的适配器，实现 contract 中的应用服务接口，在方法中执行远程调用并转换参数与返回结果

## 核心约定

- 只依赖 `contract` 的发布语言，不感知商品上下文的内部实现
- 参数与返回结果的转换在适配器内完成，调用方只见消息契约
