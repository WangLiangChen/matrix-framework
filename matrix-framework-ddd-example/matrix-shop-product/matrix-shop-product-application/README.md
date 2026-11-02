# matrix-shop-product-application

## 1. 必要的依赖
- `matrix-shop-product-domain`
- `spring-tx` 仅用于在 Application Service / 用例边界声明本地事务。

## 2. 禁止的依赖
- 禁止依赖 interface、infrastructure、client-sdk、service。
- 不依赖 REST DTO、Feign Client、JPA Entity、DAO、`EntityManager`。
- 不将 HTTP Request/Response 直接传入领域模型。

## 3. 建议的 package 结构
```text
application/
├── command/
├── query/
├── usecase/
├── service/
├── assembler/
└── exception/
```

## 4. 角色和职责
- 将北向请求转换为用例命令，加载所需聚合和版本快照。
- 协调多个聚合的读取、调用领域行为、保存聚合、发布领域事件。
- 控制事务边界；当前示例包含创建 SPU、创建 SKU 两个用例。
- 不承载商品不变量；不把跨聚合一致性变成一个超大事务。

## 5. 核心约定
- 默认一个本地事务只修改一个聚合根实例；读取其他聚合允许。
- 创建 SKU 时先加载 SPU 和其固定的 ProductType 版本，再校验规格值。
- 跨聚合写流程使用事件 / Process Manager / Saga；需要强一致时重新审视聚合边界。
- `@Transactional` 放在应用用例方法；注意 Spring 代理、自调用和事务管理器范围。
- 外部调用不应在长时间数据库事务中执行。
