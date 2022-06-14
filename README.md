# matrix-framework

A rapid development framework based on springboot,which is distributed, asynchronous non-blocking and reactive

# matrix-framework-commons

# matrix-framework-core
## 启动过程日志增强
## 配置文件外置、支持远程文件、支持profile
配置文件根路径优先级：
* System.getenv中的configRoot
* System.getProperty中的configRoot
* classpath即开发环境src/main/resources下的root.properties中的configRoot配置项
* classpath即开发环境src/main/resources
配置文件路径为：${configRoot}/matrix-framework-${profile}/
## 编程式安全获取Bean
在容器启动早期完成初始化，足够安全
```java
public class ExampleClass{
    public void exampleMethod(){
        BeanLoader.INSTANCE.getBean("");
    }
}
```
## 使用注解覆盖替换同名Bean
```java
public class Configuration{
    @OverrideBean("beanName")
    @Bean
    public Example example() {}
}
```
## AOP失效问题解决
实现接口AopProxyAware，即可获取正确的代理对象。

# matrix-framework-data
## 1、在Springboot配置类上使用注解@EnableJdbc启用模块
```java
// 标注配置类
@EnableJdbc
public class Configuration{
}
```
## 2、无侵入配置多数据源,动态切换数据源,支持嵌套
* 注解式
```java
//标注类 
@DataSourceSwitchable("dataSourceName")
public class Class{
    //标注方法
    @DataSourceSwitchable("dataSourceName")
    public void method(){}
}
```
* 编程式
```java 
MultiDataSourceContext-INSTANCE-set(datasource);
try {
    // 使用数据源的逻辑
} finally {
    MultiDataSourceContext-INSTANCE-clear();
}
```
# 多种数据访问方式，兼容原生Mybatis、Spring-jpa、Spring-jdbc



1、无侵入配置多数据源；动态切换数据源,支持数据源嵌套-
2、良好的扩展性，兼容Mybatis、JPA、Spring-jpa和Spring-jdbc。 
3、只需注解Entity类（JPA规范），即可完成功能强大、性能良好的增删改查；可指定查询返回的列;动态构建查询语句。
4、缓存和懒加载进一步提升性能 
5、启动时自动执行SQL 
6、内置序号生成器 
7、内置基于数据库的分布式锁 
8、内置基于shedlock的分布式调度

# matrix-framework-generator

# matrix-framework-web

1、兼容SpringMVC和WebFlux 
2、自动封装返回的JSON字符串为特定的格式 
3、自动封装异常(含404)为特定的格式 
4、支持SSE

