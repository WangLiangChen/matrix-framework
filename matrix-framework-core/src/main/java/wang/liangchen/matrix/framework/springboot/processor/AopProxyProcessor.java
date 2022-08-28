package wang.liangchen.matrix.framework.springboot.processor;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * @author LiangChen.Wang
 * 解决注解失效问题，获取代理对象，除本方式外，还可以用：
 * 1、直接使用applicationContext.getBean()获取自己的代理对象
 * 2、AopContext.currentProxy();使用此方法需要启动注解@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
 */
@Component
public class AopProxyProcessor implements BeanPostProcessor, ApplicationContextAware, Ordered {
    private final static String TASKSCHEDULER_THREAD_PREFIX = "taskScheduler-";
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof ThreadPoolTaskScheduler){
            ((ThreadPoolTaskScheduler)bean).setThreadNamePrefix(TASKSCHEDULER_THREAD_PREFIX);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof AopProxyAware)) {
            return bean;
        }
        AopProxyAware proxyAware = (AopProxyAware) bean;
        //如果当前对象已经是代理对象，直接注入
        if (AopUtils.isAopProxy(bean)) {
            proxyAware.setAopProxy(bean);
            return bean;
        }
        //如果不是代理对象，则获取代理对象后注入
        proxyAware.setAopProxy(applicationContext.getBean(beanName));
        return bean;
    }

    /**
     * 提升优先级 尽早初始化 防止出现 is not eligible for getting processed by...
     *
     * @return order
     */
    @Override
    public int getOrder() {
        return 0;
    }

    public interface AopProxyAware extends Aware {
        /**
         * 实现此接口的类，可以注入当前类实例的代理对象，用于解决AOP注解失效的问题
         *
         * @param proxy 代理后的对象
         */
        void setAopProxy(Object proxy);
    }
}

