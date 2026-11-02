# matrix-shop-product-service

## 1. 必要的依赖
- `matrix-shop-product-application`
- `matrix-shop-product-interface`
- `matrix-shop-product-infrastructure`
- Spring Boot Starter；如需使用本服务内部 Feign 客户端，再引入 client-sdk / OpenFeign。

## 2. 禁止的依赖
- 不承载领域模型或业务规则。
- 不允许 Domain/Application 依赖 Service。
- 不在 Bootstrap 中写商品业务流程或持久化 SQL。

## 3. 建议的 package 结构
```text
service/
├── ProductCatalogApplication.java
├── configuration/
├── seed/              # 仅 demo profile
└── monitoring/
```

## 4. 角色和职责
- 可部署运行单元：启动、Bean 装配、配置、健康检查、环境隔离。
- 组合 Interface 和 Infrastructure，不是领域模块。

## 5. 核心约定
- 生产 profile 关闭 demo seed，注入 PostgreSQL Adapter 与真实消息发布实现。
- 配置通过环境变量/配置中心注入；秘密不进入仓库。
- 服务模块才打可执行 Boot JAR；其他模块打普通 JAR。
- 架构测试应覆盖模块依赖方向和包边界。
