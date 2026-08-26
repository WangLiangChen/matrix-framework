# matrix-shop-product-infrastructure

## 允许的依赖

- `matrix-shop-product-domain`
- `matrix-framework-ddd`（根 POM 统一注入的框架规范）

## 不允许的依赖

- `matrix-shop-product-application`
- `matrix-shop-product-contract`
- `matrix-shop-product-interface`
- `matrix-shop-product-client`
- `matrix-shop-product-bootstrap`

## 角色

南向防腐层（`southbound.adapter`）：实现领域层声明的端口，适配技术实现（spring-boot-starter-data-jpa）。领域模型与技术实现在此边界双向翻译，Po 不向领域层泄漏。

## 职责

- `*RepositoryAdapter`（`@Adapter(PortType.Repository)`）：实现 `*RepositoryPort`（写模型）与 `*QueryPort`（读模型），完成聚合与持久化对象（Po）之间的翻译；重建聚合委托领域工厂 `reconstitute`；查询侧直接从 Po 装配读模型
- `*Po`（JPA 实体）与 `*Dao`（Spring Data JPA 仓储）：仅在南向适配层内部可见
- `DomainEventPublisherAdapter`（`@Adapter(PortType.Publisher)`）：实现 `DomainEventPublisherPort`，经 Spring `ApplicationEventPublisher` 发布领域事件
- `ProductJpaConfiguration`：限定 JPA 仓储与实体的扫描范围，商品与订单合并为单体应用时互不干扰

## 核心约定

- 适配器实现框架标记接口（`IRepositoryAdapter`/`IPublisherAdapter`），以 `@Adapter(PortType.*)` 标注
- 聚合重建走领域工厂 `reconstitute`，适配器不直接构造聚合
- 查询侧不建立独立读模型存储，`*QueryPort` 由同一仓储适配器从 Po 直接装配读模型
- POC 简化：领域事件在聚合保存后同步发布；生产建议以 after-commit 机制在事务提交后发布
