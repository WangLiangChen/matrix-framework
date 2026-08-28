# DDD电商POC项目

这是一个使用领域驱动设计(DDD)实现的电商POC项目, 依据如下的需求、原则和约束开发。

## 需求

* 两个限界上下文:订单-Order和商品-Product
* 通过合理的模块结构，实现这两个限界上下文，既可以独立部成微服务也可以打包成一个单体模块化应用

## 技术栈

* java 17
* spring boot
* spring data jpa
* H2（内存数据库，POC 演示用，生产可替换为 PostgreSQL）

### Product

聚合：product (spu|sku等), category (商品类目)、brand (品牌)、Attribute (一般属性、关键属性、销售属性等)

## Order

聚合：order (订单)、cart (购物车)

## 原则

* 要使用CQRS模式，但读写均使用同一个Repository，在Application层实现读写分离的逻辑即CommandService和QueryService。
* 充分理解模块结构，以实现不同的构建方式和依赖方向控制
* 要充分遵循你知道的张逸的著作《解构领域驱动设计》
* 要充分使用src/main/java/wang/liangchen/matrix/framework/ddd提供的注解和接口
*你需要对src/main/java/wang/liangchen/matrix/framework/ddd提供的注解和接口，以及文档[Guideline_DDD.md](../matrix-framework-ddd/Guideline_DDD.md)
有自己的建议，但一定基于可靠的信源。

## 约束

* 要用你已知的信息，不要用web_search,如果不可避免，一定要用可靠的、权威的信源
