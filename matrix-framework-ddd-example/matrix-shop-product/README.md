# Product Catalog PBC Sample

Java 17 / Spring Boot 4.1.2 / Spring Cloud 2025.1.4 的 DDD 多模块示例，按菱形对称架构组织。

## 模块
- `matrix-shop-product-domain`：领域模型、聚合根、值对象、南向 Port。
- `matrix-shop-product-application`：本地应用服务和用例编排。
- `matrix-shop-product-contract`：对外 Published Language。
- `matrix-shop-product-interface`：REST 北向适配器。
- `matrix-shop-product-infrastructure`：南向适配器；当前含内存实现和 PostgreSQL DDL 基线。
- `matrix-shop-product-client-sdk`：调用本 PBC 的消费者 SDK。
- `matrix-shop-product-bootstrap`：Spring Boot 部署组合。

## 领域边界
- 平台级主数据：Brand、Category、ProductType、Attribute。
- 租户级商品数据：SPU、SKU，均包含 TenantId。
- 一个 SPU 固定引用一个 ProductType 及其已发布版本；一个 ProductType 可被多个 SPU 复用。
- SPU 拥有公共属性值、分类分配、媒体和 VariantConfiguration；SKU 是独立聚合根，引用 SpuId。
- VariantConfiguration 描述 SPU 实际提供的规格维度和值，不代表 SKU；SKU 是实际存在的规格组合。
- SPU 可独立创建、维护、发布，不要求 SKU 存在。
- 价格、库存、SellerListing、SellerOffer、订单快照不属于本 PBC 核心模型。

## 构建与运行
需要 JDK 17+、Maven 3.9+。

```bash
mvn clean verify
mvn -pl matrix-shop-product-service -am spring-boot:run
```

默认 `demo` profile 使用内存 Adapter，重启后数据丢失。创建 SPU 时可使用 demo seed 生成的 ProductType / Brand / Category ID，具体 UUID 见 `docs/examples/demo-seed-ids.json`。生产环境必须替换为 PostgreSQL Adapter、认证租户上下文、Outbox Relay、真实媒体存储，并关闭 demo profile。

## API 示例
见 `docs/examples/create-spu-request.json` 和 `docs/examples/create-sku-request.json`。

## 示例范围说明
这是一个可编译的架构起步包：Domain 和 Application 的核心创建规则有实现；REST 和内存 Adapter 可启动演示。PostgreSQL SQL 是目标表结构基线，JPA/JDBC Adapter、商品查询投影、完整 CRUD、授权、审核工作流、Outbox Relay 和生产级迁移工具需要在实际环境中补齐；不要将内存 Adapter 当成生产持久化方案。
