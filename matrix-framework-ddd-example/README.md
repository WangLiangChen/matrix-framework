> 这是一个DDD的电商POC模块, 依据如下的需求、原则和约束开发。

# 需求

* 两个限界上下文订单Order和商品Product
* 这两个限界上下文，既可以独立部成微服务也可以打包成一个单体应用

# 技术栈

spring data jpa

## Product

聚合：product (spu|sku等), category (商品类目)、brand (品牌)、Attribute (一般属性、关键属性、销售属性等)

# Order

聚合：order (订单)、cart (购物车)

# 原则

* 充分理解模块结构，以实现不同的构建方式和依赖方向控制
* 要充分遵循你知道的张逸的著作《解构领域驱动设计》
* 要充分使用src/main/java/wang/liangchen/matrix/framework/ddd提供的注解和接口
*
你需要对src/main/java/wang/liangchen/matrix/framework/ddd提供的注解和接口，以及文档[Guideline_DDD.md](../matrix-framework-ddd/Guideline_DDD.md)
有自己的建议，但一定基于可靠的信源。
* 要使用CQRS模式

# 约束

* 要用你已知的信息，不要用web_search,如果不可避免，一定要用可靠的、权威的信源
