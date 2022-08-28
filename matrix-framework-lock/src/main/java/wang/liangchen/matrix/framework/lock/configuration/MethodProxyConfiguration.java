package wang.liangchen.matrix.framework.lock.configuration;

import org.springframework.aop.Advisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import wang.liangchen.matrix.framework.lock.aop.advisor.LockableMethodAdvisor;
import wang.liangchen.matrix.framework.lock.core.LockManager;

/**
 * @author Liangchen.Wang 2022-08-27 22:00
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class MethodProxyConfiguration {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor lockableMethodAdvisor(@Lazy LockManager lockManager) {
        return new LockableMethodAdvisor(lockManager);
    }

}
