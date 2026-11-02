# matrix-shop-product-infrastructure

## 1. 必要的依赖
- `matrix-shop-product-domain`
- Spring Context（注册 Adapter Bean）。
- 生产持久化可增加 Spring Data JPA / JDBC、PostgreSQL Driver、Jackson（只在 Adapter 内使用）。

## 2. 禁止的依赖
- Domain 禁止依赖 Infrastructure；Infrastructure 不反向改变 Domain 模型。
- 禁止依赖 interface、client-sdk、service。
- 不把 JPA Entity / DAO 作为 Repository Port 返回值。

## 3. 建议的 package 结构
```text
infrastructure/
├── south/adapter/
│   ├── persistence/{jpa,jdbc,memory}/
│   ├── client/{feign,http}/
│   ├── messaging/{outbox,mq}/
│   └── storage/
├── configuration/
└── migration/
```

## 4. 角色和职责
- 实现 Domain 的 Repository / Publisher / Client Port。
- 负责 ORM/SQL 映射、Outbox 持久化、外部系统 ACL、技术配置。
- 本包当前提供内存 Adapter 以便示例启动，并附 PostgreSQL DDL 基线。

## 5. 核心约定
- 内存 Adapter 仅供本地演示/测试；重启数据丢失，不可用于生产。
- 生产环境应实现 PostgreSQL Repository Adapter；唯一键：tenant+SPU code、tenant+SKU code、tenant+SPU+variant_key。
- SPU/SKU 动态属性写模型可用 JSONB 保存；如需高频筛选，建立独立查询投影/倒排索引，避免把所有查询压到聚合 JSONB。
- Outbox 记录与聚合更新必须同库同事务；Relay 至少一次投递，消费者按 eventId 幂等。
- Category 树环检测、SKU 规格值引用检查应在应用流程和数据库约束两侧共同保护。
