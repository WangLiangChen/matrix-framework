# matrix-framework

A rapid,efficient and stable development framework based on springboot,which is distributed, asynchronous non-blocking
and reactive

# 配置仓库

因snapshot版本在中央仓库不存在，故需指定如下仓库：

```xml

<repositories>
    <repository>
        <id>matrix-snapshot</id>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

# Maven依赖

matrix-framework的依赖是分模块独立依赖的，后续的模块描述会有相应的maven依赖说明,但需要引入springboot的依赖

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>2.7.4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

# matrix-framework-commons

# matrix-framework-core

## 配置文件

配置文件支持外置、支持远程文件、支持profile。
<br>配置文件的根路径：${configRoot}/matrix-framework-${profile}/
<br>配置文件根路径configRoot优先级：

* System.getenv中的configRoot
* System.getProperty中的configRoot
* classpath
* 在bootstrap 或 application 中指定 spring.config.import=matrix://file://d:

## 编程式安全获取Bean

在容器启动早期完成初始化，足够安全

```java
public class ExampleClass {
    public void exampleMethod() {
        Object bean = BeanLoader.INSTANCE.getBean("beanName");
        Object bean = BeanLoader.INSTANCE.getBean(Bean.class);
    }
}
```

## 覆盖替换同名Bean

可以使用如下注解覆盖同名的SpringBean

```java
public class Configuration {
    @OverrideBean("beanName")
    @Bean
    public Example example() {
    }
}
```

## AOP失效问题解决

实现接口AopProxyAware，即可获取正确的代理对象。

# matrix-framework-data

## maven依赖

```xml

<dependency>
    <groupId>wang.liangchen.matrix</groupId>
    <artifactId>matrix-framework-data-spring-boot-starter</artifactId>
    <version>${version.version}</version>
</dependency>
```

## 启用

在Springboot配置类上使用注解@EnableJdbc启用模块

```java

@EnableJdbc
public class Configuration {
}
```

## 启用自动缓存

在引入matrix-cache的情况下，基于StandaloneDao的操作会自动使用缓存。
<br>maven引入说明：<https://github.com/WangLiangChen/matrix-cache/blob/1.0.0/README.md>

* 自动缓存数据的操作
  <br>select list pagination count exists
* 自动清理缓存的操作
  <br>insert delete update
* 可以禁用缓存的方法参数
  <br>Criteria DeleteCriteria UpdateCriteria

## 多数据源

动态切换数据源,支持方法间数据源嵌套

* 多数据源配置 jdbc.properties

```properties
# primary is required
primary.dialect=wang.liangchen.matrix.framework.data.datasource.dialect.MySQLDialect
primary.datasource=com.zaxxer.hikari.HikariDataSource
#primary.url=
primary.host=127.0.0.1
primary.port=3306
primary.database=
#primary.schema=
primary.username=
primary.password=
#primary.extra.MaximumPoolSize=100
#primary.extra.maximum-pool-size=100
# other is optional
one.dialect=wang.liangchen.matrix.framework.data.datasource.dialect.PostgreSQLDialect
one.datasource=com.zaxxer.hikari.HikariDataSource
one.host=127.0.0.1
one.port=5432
one.database=
#one.schema=
one.username=
one.password=
```

* 注解式切换

```java
//标注类 
@DataSourceSwitchable("dataSourceName")
public class Class {
    //标注方法
    @DataSourceSwitchable("dataSourceName")
    public void method() {
    }
}
```

* 编程式切换

```java 
MultiDataSourceContext.INSTANCE.set(datasource);
try {
    // 使用数据源的逻辑
} finally {
    MultiDataSourceContext.INSTANCE.clear();
}
```

## 基于数据库的序列号生成器
```java
public class ExampleClass {
    @Inject
    ISequenceDao sequenceDao;

    public Long sequence() {
        return sequenceDao.sequenceNumber(sequenceKey, initialValue);
    }
}
```
# 分布式锁
## Maven依赖
```xml
<dependency>
  <groupId>wang.liangchen.matrix</groupId>
  <artifactId>matrix-framework-lock-spring-boot-starter</artifactId>
</dependency>
```
## 启用
使用注解@EnableLock开启分布式锁
```java
@EnableLock
```
使用注解@MatrixLock配置锁,指定锁名称和最大最小锁定时长
```java
@MatrixLock(lockKey="",lockAtLeast="",lockAtMost="")
```


# matrix-framework-generator

# matrix-framework-web

1、兼容SpringMVC和WebFlux
2、自动封装返回的JSON字符串为特定的格式
3、自动封装异常(含404)为特定的格式
4、支持SSE

# matrix-framework的使用

## 引入数据访问模块

```xml

<dependency>
    <groupId>wang.liangchen.matrix</groupId>
    <artifactId>matrix-framework-data-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 在src/main/resources目录创建matrix-framework目录用来放置如下文件

1、
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