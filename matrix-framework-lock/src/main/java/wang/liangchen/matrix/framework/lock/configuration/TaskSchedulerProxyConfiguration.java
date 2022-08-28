package wang.liangchen.matrix.framework.lock.configuration;

import org.springframework.aop.Advisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import wang.liangchen.matrix.framework.lock.aop.advisor.LockableTaskSchedulerInterceptor;
import wang.liangchen.matrix.framework.lock.core.LockManager;
import wang.liangchen.matrix.framework.springboot.aop.advisor.TaskSchedulerAdvisor;

/**
 * @author Liangchen.Wang 2022-08-27 22:00
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class TaskSchedulerProxyConfiguration {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor lockableTaskSchedulerAdvisor(@Lazy LockManager lockManager) {
        return new TaskSchedulerAdvisor(new LockableTaskSchedulerInterceptor(lockManager));
    }
}
