# matrix-shop-product-application

## 角色

应用服务层（本地北向：`northbound.local`），负责协调领域对象、实现业务用例。位于六边形架构中领域层与远程北向（`interfaces`）之间：对外为 `interfaces` 提供本地用例接口，对内编排 `domain` 的聚合、领域工厂与端口。

## 职责

- 实现业务用例：命令服务（`*CommandApplicationService`）编排聚合完成状态变更，查询服务（`*QueryApplicationService`）经 `*RepositoryPort` 只读获取聚合并装配为视图
- 用例的入参与返回使用 `contract` 的消息契约（`message.request` 请求、`message.response` 响应/视图），不向外部暴露领域模型对象
- 管理事务边界：命令用例以 `@Transactional` 标注，一个用例一个事务
- 发布领域事件：聚合保存后统一发布聚合收集的领域事件
- 异常翻译：捕获领域异常并包装为应用异常（`northbound.exception.ApplicationException`），附加用例上下文

## 允许的依赖

- `matrix-shop-product-contract`
- `matrix-shop-product-domain`
- `matrix-framework-ddd`（框架注解与标记接口，父 POM 统一管理的框架规范）

## 不允许的依赖

- `matrix-shop-product-interface`（远程北向位于应用层之上）
- `matrix-shop-product-infrastructure`（南向适配位于应用层之下，端口实现于运行期注入）
- `matrix-shop-product-client`
- `matrix-shop-product-bootstrap`

## 核心约定

- 服务以 `@Service` + `@ApplicationService(ApplicationServiceType.COMMAND|QUERY)` 标注，分别实现框架标记接口 `ICommandApplicationService`/`IQueryApplicationService`
- 命令/查询分离（CQRS）：查询服务经 `*RepositoryPort` 只读获取聚合（findById 与统一语言命名的查询方法），不经领域模型变更路径；类目树等展示形状由应用层从聚合装配
- 一个用例一个事务，一次事务只修改一个聚合实例；领域事件在聚合保存后统一发布（`eventPublisher.publish(product.events())` 之后 `product.clearEvents()`）
- 用例横切关注点 `useCase` 捕获 `AbstractDomainException` 并包装为 `ApplicationException`（继承 `AbstractApplicationException`），领域异常不向远程北向泄漏
- 不包含领域逻辑，只做流程编排；业务规则由领域模型裁决，应用服务仅触发
- 依赖以构造器注入；需要 `infrastructure` 实现的接口均在 `domain.port` 中声明，应用层不感知具体实现
