# matrix-framework

A rapid development framework based on springboot,which is distributed, asynchronous non-blocking and reactive

# matrix.framework.commons

1、功能强大、性能良好且充分测试的工具类库

# matrix.framework.springboot
1、AopProxyAware，实现该接口的类可以将代理对象注入，用于解决类调用本类方法时，动态代理失效的问题
2、OverrideBean，使用该注解，可以覆盖同名的Bean
3、启动过程跟踪
4、初始化包外配置

1、使用注解@OverrideBean覆盖重名的Bean

# matrix.framework.web

1、兼容SpringMVC和WebFlux 2、自动封装返回的JSON字符串为特定的格式 3、自动封装异常(含404)为特定的格式 4、支持SSE

# matrix.framework.data

1、无侵入配置多数据源；动态切换数据源。 2、良好的扩展性，兼容Mybatis、JPA、Spring-jpa和Spring-jdbc。 3、只需Entity和Query类，即可完成功能强大、性能良好的增删改查；可指定查询返回的列。
4、自动完成underline和camel case的互转 5、缓存和懒加载进一步提升性能 6、启动时自动执行SQL 7、内置序号生成器 8、内置基于数据库的分布式锁 9、内置基于shedlock的分布式调度
