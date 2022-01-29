package wang.liangchen.matrix.framework.springboot.configuration;

import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Liangchen.Wang
 * 注解@Configuration 中所有带 @Bean 注解的方法都会被动态代理，因此调用该方法返回的都是同一个实例。ConfigurationClassPostProcessor
 * SpringBoot2 默认使用Cglib动态代理,使用注解(exposeProxy)后，用AopContext.currentProxy()获取当前代理对象，用以避免事务/缓存等注解失效
 */
@EnableAspectJAutoProxy(exposeProxy = true)
public class RootAutoConfiguration {
}
