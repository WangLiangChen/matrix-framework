package wang.liangchen.matrix.framework.springboot.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Liangchen.Wang
 */
@AutoConfiguration
@EnableAspectJAutoProxy(exposeProxy = true)
public class RootAutoConfiguration {
}
