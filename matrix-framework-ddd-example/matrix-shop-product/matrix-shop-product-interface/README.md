# matrix-shop-product-interface

## 1. 必要的依赖
- `matrix-shop-product-application`
- `matrix-shop-product-contract`
- `spring-boot-starter-webmvc`（HTTP 适配器）

## 2. 禁止的依赖
- 禁止依赖 infrastructure、service、client-sdk。
- Controller 不直接访问 Repository Port / DAO / EntityManager。
- 不允许 Contract DTO 穿透到 Domain。

## 3. 建议的 package 结构
```text
interfaceadapter/north/remote/
├── rest/controller/
├── rest/mapper/
├── rpc/provider/
├── messaging/subscriber/
└── exception/
```

## 4. 角色和职责
- 北向远程适配器：REST Controller、RPC Provider、MQ Subscriber 等。
- 做协议解析、格式校验、Contract 到 Application Command 的映射。
- 返回 Contract Response，不暴露 Domain 类型。

## 5. 核心约定
- Controller 保持薄；业务规则放 Domain，流程编排放 Application。
- URL 带租户范围；租户身份必须由认证上下文确定，不能盲信请求体。
- 统一异常映射；不要把内部堆栈或数据库异常暴露给调用方。
- 示例接口仅用于演示，生产需接入认证授权、限流、审计和幂等策略。
