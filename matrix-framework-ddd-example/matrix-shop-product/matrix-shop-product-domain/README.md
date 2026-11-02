# matrix-shop-product-domain

## 1. 必要的依赖
- 仅依赖 Java 17 标准库；测试范围允许 JUnit 5。
- 不依赖 Spring、JPA、Jackson、Web、消息中间件或数据库驱动。

## 2. 禁止的依赖
- 禁止依赖 application、contract、interface、infrastructure、client-sdk、service。
- 禁止在领域模型中出现 `@Entity`、`@Transactional`、`@Json...`、HTTP/MQ/JPA 类型。
- Aggregate、Entity、Value Object 不直接调用远程服务；外部能力通过 `south.port` 抽象。

## 3. 建议的 package 结构
```text
domain/
├── aggregate/{spu,sku,brand,category,producttype,attribute}
├── shared/{id,attribute}
├── service/
├── event/
├── exception/
└── south/port/{repository,publisher,client}
```

## 4. 角色和职责
- `Spu`：标准商品身份、公共信息、SPU 属性值、分类关系、规格配置；可独立于 SKU 存在。
- `Sku`：真实存在的规格组合、SKU 属性、条码、包装信息；库存和价格不在本聚合。
- `Brand` / `Category`：平台级品牌身份和商品导航分类。
- `ProductType`：平台级商品类型模板，发布不可变版本；SPU 固定引用某个版本。
- `Attribute`：可复用的全局属性定义和稳定选项编码。
- `ProductTypeAttribute`：属性在某商品类型中的 Scope、Role、required、filterable 等使用规则。
- `VariantConfiguration`：SPU 实际提供的规格维度及可选值，不代表 SKU 实体。

## 5. 核心约定
- 聚合外只引用 ID；SPU 与 SKU 是独立聚合根。
- `ProductType` 与 SPU 是 1:N；一个 SPU 固定一个 ProductType 及版本。
- SPU 可以 0 个 SKU；SPU 发布不以 SKU 存在为领域不变量。
- SKU 必须匹配 SPU 的 `VariantConfiguration`；无规格维度时使用 `_DEFAULT` 组合键。
- 规格维度必须为单值 SKU ENUM；属性值使用强类型 `AttributeValue`，禁止把所有值都当 String。
- Attribute option code 稳定；使用中的选项应停用，不直接删除或复用 code。
- 数据库唯一约束是最终并发防线；应用层 exists 检查只用于友好报错。
