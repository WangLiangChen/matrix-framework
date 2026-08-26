# matrix-shop-product-domain

## 允许的依赖

- `matrix-framework-ddd`（父 POM 统一管理的框架规范）

## 不允许的依赖

- 任何业务模块：`contract`、`application`、`infrastructure`、`interfaces`、`client`、`bootstrap`
- 任何技术实现框架（Spring、JPA/Hibernate 等）；持久化对象（Po）只存在于 `infrastructure`，不得向领域层泄漏

## 角色

领域层，业务核心：实现领域模型与业务规则（不变式），声明南向端口。处于六边形架构的中心，是全系统最稳定的部分，只依赖领域元模型。

## 职责

- 定义领域模型：聚合根（`Product`、`Category`、`Brand`、`Attribute`）、聚合内部实体（`Sku`）、值对象（`Money`、`AttributeValueRef`）、身份标识（`*Id`）
- 实现领域不变式（业务规则），违反业务规则时抛出 `DomainException`
- 领域工厂封装聚合的创建（`create`）与重建（`reconstitute`）
- 定义领域事件（`ProductCreatedEvent`、`ProductListedEvent`、`ProductDelistedEvent`、`SkuPriceChangedEvent`、`CategoryCreatedEvent`、`CategoryMovedEvent`、`BrandCreatedEvent`、`AttributeCreatedEvent`），记录领域事实
- 声明南向端口（`domain.port`）：`*RepositoryPort`、`*QueryPort`、`DomainEventPublisherPort`，由 `infrastructure` 实现
- 定义读模型（`domain.readmodel`），供查询侧（CQRS 的 Q）使用

## 核心约定

- 每个聚合一个独立包（`product`、`category`、`brand`、`attribute`），package-info 以 `@AggregatePackage` 标注；领域根包以 `@DomainPackage` 标注
- 领域元素以 `@DomainModel(DomainMetaModel.*)` 标注元模型类型（AggregateRoot/Entity/ValueObject/Identity/DomainEvent/DomainFactory）
- 聚合外部只能通过聚合根访问与修改内部状态；`Sku` 是 `product` 聚合内部实体，包内可见（非 public），不对外暴露
- 聚合之间通过身份标识（`CategoryId`、`BrandId`、`AttributeId`）引用，不持有对象引用
- 领域事件类名以 `Event` 结尾（如 `ProductCreatedEvent`）
- 领域事件由聚合自身收集（`AbstractAggregateRoot#raise`），经 `DomainEventPublisherPort` 发布；创建类事件仅由领域工厂调用
- 工厂分工：全新聚合走 `create`，从持久化数据重建走 `reconstitute`，重建由仓储适配器委托领域工厂完成
- 身份标识使用 record 实现 `ISimpleIdentity`，`generate()` 生成无业务含义的代理标识
- 业务规则违反统一抛出 `DomainException`（继承 `AbstractDomainException`），消息使用统一语言描述业务含义
