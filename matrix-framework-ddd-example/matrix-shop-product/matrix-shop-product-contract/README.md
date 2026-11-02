# matrix-shop-product-contract

## 1. 必要的依赖
- 仅依赖 Java 17 标准库；DTO 使用 record。

## 2. 禁止的依赖
- 禁止依赖 domain、application、interface、infrastructure、client-sdk、service。
- 禁止向 DTO 暴露 Domain Aggregate、Entity、Value Object 或 Repository。
- 不在 Contract 中使用 JPA 注解或 Spring MVC / Feign 注解。

## 3. 建议的 package 结构
```text
contract/message/
├── request/
├── response/
├── event/
└── service/
```

## 4. 角色和职责
- Product Catalog 对其他 PBC / 调用方发布的 Published Language。
- 定义稳定的请求、响应和集成事件结构。
- Contract 是 API / 消息边界，不是内部领域模型的复制品。

## 5. 核心约定
- 字段语义和兼容性优先于内部类名；DTO 与 Domain 之间必须映射。
- 新增可选字段优先保持向后兼容；破坏性修改升级版本。
- 不把库存、价格、商家 Offer 的内部模型塞进 Product Catalog Contract。
- 对外 ID 使用 UUID 字符串/UUID；枚举值使用稳定 code，不使用本地化展示文案。
