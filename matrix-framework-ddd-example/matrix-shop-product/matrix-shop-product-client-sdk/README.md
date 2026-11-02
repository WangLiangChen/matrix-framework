# matrix-shop-product-client-sdk

## 1. 必要的依赖
- `matrix-shop-product-contract`
- Spring Cloud OpenFeign（仅当采用 Feign 客户端时）。

## 2. 禁止的依赖
- 禁止依赖 domain、application、infrastructure、interface、service。
- 不把 Product 内部 Repository / Aggregate 暴露给消费者。

## 3. 建议的 package 结构
```text
client/
├── ProductCatalogFeignClient.java
├── configuration/
├── fallback/
└── exception/
```

## 4. 角色和职责
- 消费者侧 Client SDK，用于调用 Product Catalog 的北向 HTTP API。
- 不等同于 Product Catalog 调用 Inventory / Pricing 的出站 Client Adapter；后者属于 Infrastructure。

## 5. 核心约定
- 只依赖 Published Language Contract。
- URL、超时、认证、重试和熔断配置外置。
- 非幂等创建请求谨慎重试；推荐传递幂等键。
- 错误码应映射为消费者可理解的 SDK 异常，不暴露服务内部异常。
