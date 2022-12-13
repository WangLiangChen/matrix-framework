package wang.liangchen.matrix.framework.springboot.configuration;

import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Liangchen.Wang
 */
@EnableAspectJAutoProxy(exposeProxy = true)
public class RootAutoConfiguration {
    // @PropertySource>@ComponentScan>@Import>@ImportResource>@Bean>接口的默认方法>父类

}
