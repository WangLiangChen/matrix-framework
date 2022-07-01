package wang.liangchen.matrix.framework.springboot.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Liangchen.Wang
 * ConfigurationClassParser的解析顺序:
 * @PropertySource->@ComponentScan->@Import->@ImportResource->@Bean->接口的默认方法->父类
 */
@AutoConfiguration
@EnableAspectJAutoProxy(exposeProxy = true)
public class RootAutoConfiguration {
}
