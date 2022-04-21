# matrix-framework

A rapid development framework based on springboot,which is distributed, asynchronous non-blocking and reactive

# matrix.framework.commons

1、功能强大、性能良好且充分测试的通用工具类库

# matrix.framework.core
1、AopProxyAware，实现该接口的类可以将代理对象注入，用于解决类调用本类方法时，动态代理失效的问题
2、OverrideBean，使用该注解，可以覆盖同名的Bean
3、配置外置，支持远程

# matrix.framework.data

1、无侵入配置多数据源；动态切换数据源,支持数据源嵌套.
2、良好的扩展性，兼容Mybatis、JPA、Spring-jpa和Spring-jdbc。 
3、只需注解Entity类（JPA规范），即可完成功能强大、性能良好的增删改查；可指定查询返回的列;动态构建查询语句。
4、缓存和懒加载进一步提升性能 
5、启动时自动执行SQL 
6、内置序号生成器 
7、内置基于数据库的分布式锁 
8、内置基于shedlock的分布式调度

# matrix.framework.web

1、兼容SpringMVC和WebFlux 
2、自动封装返回的JSON字符串为特定的格式 
3、自动封装异常(含404)为特定的格式 
4、支持SSE

