package liangchen.wang.matrix.framework.springboot.processor;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author LiangChen.Wang
 */
@Component
public class CurrentProxyProcessor implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof CurrentProxyAware)) {
            return bean;
        }
        CurrentProxyAware proxy = (CurrentProxyAware) bean;
        if (AopUtils.isAopProxy(bean)) {//如果当前对象已经是代理对象，直接注入
            proxy.setCurrentProxy(bean);
        } else {//如果不是代理对象，则获取代理对象后注入
            proxy.setCurrentProxy(applicationContext.getBean(beanName));
            //获取代理对象有两个方法,故解决注解失效有三类方法，本方法和下面的两种
            //1 applicationContext.getBean()
            //2 AopContext.currentProxy();使用此方法需要启动注解@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
        }
        return bean;
    }

    //实现此接口的类，可以注入当前类实例的代理对象，用于解决AOP注解失效的问题
    public interface CurrentProxyAware {
        void setCurrentProxy(Object proxy);
    }
}
