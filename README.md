# matrix-framework

A rapid development framework based on springboot,which is distributed, asynchronous non-blocking and reactive

# matrix-framework-commons

# matrix-framework-core
## 启动过程日志增强
## 配置文件外置、支持远程文件、支持profile
配置文件根路径configRoot优先级：
* System.getenv中的configRoot
* System.getProperty中的configRoot
* classpath
* 在bootstrap 或 application 中指定 spring.config.import=matrix://file://d:
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

# matrix-framework的使用
## 新建maven项目引入springboot依赖
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>2.7.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```
## 引入数据访问模块
```xml
<dependency>
    <groupId>wang.liangchen.matrix</groupId>
    <artifactId>matrix-framework-data-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
## 在src/main/resources目录创建matrix-framework目录用来放置如下文件
1、jdbc.properties
```properties
#primary is required
primary.dialect=wang.liangchen.matrix.framework.data.datasource.dialect.MySQLDialect
primary.datasource=com.zaxxer.hikari.HikariDataSource
#primary.url=
primary.host=127.0.0.1
primary.port=5432
primary.database=
#primary.schema=
primary.username=
primary.password=
#primary.extra.MaximumPoolSize=100
#primary.extra.maximum-pool-size=100
```
2、autoscan.properties 内容为空
3、logger.properties
```properties
config.file=logback.xml
```
4、logback.xml
```xml
<configuration scan="true" debug="false">
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%date{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %caller{1} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="ERROR">
        <appender-ref ref="CONSOLE"/>
    </root>
    <logger name="**************" level="DEBUG"/>
    <logger name="wang.liangchen.matrix" level="DEBUG"/>
    <logger name="org.hibernate.SQL" level="DEBUG"/>
    <logger name="org.hibernate.type.descriptor.sql.BasicBinder" level="TRACE"/>
    <logger name="org.springframework.jdbc.core.JdbcTemplate" level="DEBUG"/>
    <logger name="org.springframework.jdbc.core.StatementCreatorUtils" level="TRACE"/>
</configuration>
```