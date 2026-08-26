# matrix-shop-product-bootstrap

## 允许的依赖

- `matrix-shop-product-interface`
- `matrix-shop-product-infrastructure`
- `matrix-framework-ddd`（根 POM 统一注入的框架规范）

## 不允许的依赖

- `matrix-shop-product-domain`
- `matrix-shop-product-application`
- `matrix-shop-product-contract`
- `matrix-shop-product-client`

## 角色

组合根（Composition Root），微服务入口：装配各模块并启动应用，是唯一允许了解全部模块装配关系的模块。

## 职责

- `ProductApplication`：以商品限界上下文为边界的独立微服务入口（`@SpringBootApplication(scanBasePackages = "wang.liangchen.matrix.shop.product")`）
- `application.yml`：应用配置——应用名、H2 内存数据源、JPA `ddl-auto=create-drop`、`open-in-view=false`、端口 8081
- `ArchitectureTest`：架构守护测试，引用框架规则集 `DddArchitectureRules`（分层依赖规则、领域模型规则、消息契约规则、包标注规则、架构标注规则），守护整个限界上下文的架构

## 核心约定

- 组合根只做装配与配置，不包含业务逻辑
- 内层模块（domain、application、contract）只经传递依赖可见，bootstrap 不直接引用
- 架构守护测试随构建执行，违反架构规则即构建失败
