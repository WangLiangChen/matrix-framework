# 商品限界上下文(Product)

## 战略设计

- 子域类型：核心域(Core)
- 统一语言边界：商品目录（SPU/SKU、类目、品牌、属性及其类型）
- 上下文映射：
  - 商品上下文是订单上下文的上游，协作模式为**客户-供应商**；
  - 商品上下文通过**开放主机服务**（`ProductResource`，RemoteType.Resource）向下游提供发布语言 `ProductDetailView`；
  - 订单上下文在自己的边界内以**防腐层**（`ProductClientPort`/`ProductClientAdapter`）翻译本上下文的发布语言，不共享领域模型；
  - 与订单上下文当前为同步 REST 集成（下游按需拉取商品快照），未引入事件驱动的发布/订阅集成，contract 因此不含事件契约（有意取舍，非缺位）；若演进为事件驱动集成，在 contract 的 message 事件包补充 `AbstractContractEvent` 派生的契约事件（如 `SkuPriceChanged`）并由开放主机服务发布。

## 统一语言词汇表

| 术语 | 英文命名 | 精确定义 |
|---|---|---|
| 商品 | Product | 标准产品单元(SPU)，聚合根，内含多个SKU |
| SKU | Sku | 具体可售的销售单元（如"黑色/64G"），聚合内部实体 |
| 类目 | Category | 商品的层级分类，通过父类目标识引用上级 |
| 品牌 | Brand | 商品的品牌 |
| 属性 | Attribute | 商品属性定义，含一般属性、关键属性、销售属性 |
| 属性值引用 | AttributeValueRef | 商品对某属性（AttributeId）的取值 |
| 金额 | Money | 商品销售价格，数额+币种 |
| 上架/下架 | putOnSale/takeOffSale | 商品进入/退出可售状态 |

## 战术设计

四个聚合（每个聚合一个独立包，包内 package-info 标注 @AggregatePackage）：

1. **product 聚合**：SPU 为聚合根，SKU 为内部实体（包内可见）；不变式：SKU 价格必须大于零、库存扣减不得低于零；通过 CategoryId/BrandId/AttributeId 引用其它聚合。
2. **category 聚合**：类目为聚合根；不变式：类目不能移动到自身。
3. **brand 聚合**：品牌为聚合根；不变式：品牌名称不能为空。
4. **attribute 聚合**：属性为聚合根，类型为一般/关键/销售；不变式：属性选项不得重复。

领域事件：ProductCreated、ProductListed、ProductDelisted、SkuPriceChanged、CategoryCreated、CategoryMoved、BrandCreated、AttributeCreated。

## 领域建模约定（框架元模型）

- 每个聚合一个独立包，包级 `package-info` 标注 `@AggregatePackage`，领域根包标注 `@DomainPackage`；
- 领域元素以 `@DomainModel(DomainMetaModel.*)` 标注元模型类型：AggregateRoot/Entity/ValueObject/Identity/DomainEvent/DomainFactory；
- 聚合外部只能通过聚合根访问与修改内部状态；`Sku` 是 product 聚合内部实体（包内可见），不对外暴露；
- 聚合之间通过身份标识（`CategoryId`/`BrandId`/`AttributeId`）引用，不持有对象引用；
- 领域事件类名以 `Event` 结尾，由聚合自身收集（`AbstractAggregateRoot#raise`）并经 `DomainEventPublisherPort` 发布；创建类事件仅由领域工厂调用；
- 工厂分工：全新聚合走 `create`，从持久化数据重建走 `reconstitute`，重建由仓储适配器委托领域工厂完成；
- 身份标识以 record 实现 `ISimpleIdentity`，`generate()` 生成无业务含义的代理标识；
- 业务规则违反统一抛出 `DomainException`（继承 `AbstractDomainException`），消息使用统一语言描述业务含义。

## CQRS

- 命令侧：`*CommandApplicationService` → 聚合 → `*RepositoryPort`；
- 查询侧：`*QueryApplicationService` → `*RepositoryPort`（findById 与统一语言命名的查询方法返回聚合根）→ 聚合 → View；
- 仓储以聚合根为读写单位，重建聚合由仓储适配器委托领域工厂 reconstitute。

## 模块结构（依赖方向）

```
matrix-shop-product-bootstrap（组合根，微服务入口）
  ├─ matrix-shop-product-interface（北向远程：northbound.remote，Controller/Resource）
  ├─ matrix-shop-product-application（应用服务：northbound.local，命令/查询分离）
  ├─ matrix-shop-product-contract（消息契约：message 请求/响应/视图 + service 应用服务接口）
  ├─ matrix-shop-product-client（下游客户端 SDK：client，远程调用开放主机服务）
  ├─ matrix-shop-product-infrastructure（南向防腐层：southbound.adapter，JPA适配器）
  └─ matrix-shop-product-domain（领域模型+端口：domain 与 domain.port）
```

依赖规则：interface→application→domain←infrastructure，contract 为发布语言被 interface/application/client 引用；contract 的 `service` 包定义面向下游的应用服务接口（`ProductQueryService`），单体形态由应用服务本地实现，微服务形态由 client 模块的 `ProductFeignClientAdapter` 远程实现（传输为 Spring RestClient，未引入 Spring Cloud），下游只依赖 contract（按需引入 client）即可不感知部署形态；领域层只允许依赖 `matrix-framework-ddd`（父 POM 统一管理的框架规范），不依赖任何业务模块（contract/application/infrastructure/interface/client/bootstrap）与技术实现框架（Spring、JPA/Hibernate 等）；持久化对象（Po）只存在于 infrastructure，不向领域层泄漏。
