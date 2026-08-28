# 订单限界上下文(Order)

## 战略设计

- 子域类型：核心域(Core)
- 统一语言边界：订单交易（下单、支付、发货、完成、取消）、购物车（加购、改量、移除、清空）
- 上下文映射：
  - 订单上下文是商品上下文的下游，协作模式为**客户-供应商**；
  - 订单上下文在自己的边界内以**防腐层**（`ProductClientPort`/`ProductClientAdapter`）调用商品上下文的开放主机服务并翻译其发布语言（`ProductDetailView`），上游模型不腐化订单领域模型；
  - 订单领域使用自己的 `ProductId`、`Money` 等类型（语义分歧），与商品上下文同名类型只在边界处转换。

## 统一语言词汇表

| 术语 | 英文命名 | 精确定义 |
|---|---|---|
| 订单 | Order | 买家的下单交易，聚合根，维护订单状态机 |
| 订单项 | OrderItem | 订单内的商品快照（名称、单价），聚合内部实体 |
| 下单 | placeOrder | 创建待支付订单，商品信息在下单时快照 |
| 支付/发货/完成/取消 | pay/ship/complete/cancel | 订单状态机的合法迁移 |
| 购物车 | Cart | 买家的待购商品集合，聚合根 |
| 购物车项 | CartItem | 购物车内的商品快照，同一商品只有一个购物车项 |
| 买家 | UserId | 买家身份标识（外部用户实体的身份引用） |
| 商品摘要 | ProductSummary | 订单上下文对商品的理解（名称+最低售价），防腐层翻译产物 |
| 交易项 | TradeItemSummary | 订单与购物车共享的交易行快照（商品+名称+单价+数量），聚合对外暴露与工厂重建的统一形态，置于共享内核包（domain.shared） |

## 战术设计

1. **order 聚合**：订单为聚合根，订单项为内部实体；不变式：状态机迁移合法（支付仅限待支付、发货仅限已支付、完成仅限已发货、取消仅限待支付）、订单必须包含商品。
2. **cart 聚合**：购物车为聚合根，购物车项为内部实体；不变式：同一商品只有一个购物车项、商品数量必须大于零。

领域事件：OrderPlaced、OrderPaid、OrderShipped、OrderCompleted、OrderCanceled、CartItemAdded。

3. **共享内核**（domain.shared）：订单与购物车两个聚合共享的值对象与身份标识——Money（金额）、ProductId（商品身份标识）、ProductSummary（商品摘要）、TradeItemSummary（交易项摘要，购物车项与订单项的统一快照形态），不属于任何单一聚合。

## CQRS

- 命令侧：`OrderCommandApplicationService`/`CartCommandApplicationService`（`northbound.local`）→ 聚合 → `*RepositoryPort`（`domain.port`）；
- 查询侧：`OrderQueryApplicationService`/`CartQueryApplicationService`（`northbound.local`）→ `*RepositoryPort`（findById 与统一语言命名的查询方法返回聚合根）→ 聚合 → View；
- 加购与下单通过 `ProductClientPort`（客户端端口，防腐层）获取商品名称与最低售价快照。

## 模块结构（依赖方向）

```
matrix-shop-order-bootstrap（组合根，微服务入口）
  ├─ matrix-shop-order-interface（北向远程：northbound.remote，Controller）
  ├─ matrix-shop-order-application（应用服务：northbound.local，命令/查询分离）
  ├─ matrix-shop-order-contract（消息契约：message 请求/响应/视图 + service 应用服务接口）
  ├─ matrix-shop-order-client（下游客户端 SDK：client，远程调用北向远程服务）
  ├─ matrix-shop-order-infrastructure（南向防腐层：southbound.adapter，JPA适配器+商品客户端适配器）
  └─ matrix-shop-order-domain（领域模型 + 端口 domain/port）
```

依赖规则同商品上下文；contract 的 `service` 包定义面向下游的应用服务接口（`OrderCommandService`/`OrderQueryService`），单体形态由应用服务本地实现，微服务形态由 client 模块的 `OrderFeignClientAdapter` 远程实现（传输为 Spring RestClient，未引入 Spring Cloud）；额外约定：order-infrastructure 依赖 product-contract（上游发布语言），且仅在南向适配器内翻译，领域层不感知商品上下文类型。
